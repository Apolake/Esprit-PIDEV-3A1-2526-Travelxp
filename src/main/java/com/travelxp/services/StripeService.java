package com.travelxp.services;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.sun.net.httpserver.HttpServer;

/**
 * Service for handling Stripe Checkout payments.
 * Creates a Stripe Checkout Session and opens the hosted payment page in the browser.
 * Listens for the success redirect on a local HTTP server to confirm payment.
 */
public class StripeService {

    private static final int CALLBACK_PORT = 4242;
    private static final String SUCCESS_PATH = "/stripe/success";
    private static final String CANCEL_PATH = "/stripe/cancel";
    private static final String SUCCESS_URL = "http://localhost:" + CALLBACK_PORT + SUCCESS_PATH;
    private static final String CANCEL_URL = "http://localhost:" + CALLBACK_PORT + CANCEL_PATH;

    private String apiKey;

    public StripeService() {
        loadApiKey();
    }

    private void loadApiKey() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);
                apiKey = props.getProperty("stripe.secret.key", "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("WARNING: stripe.secret.key not found in db.properties. Stripe payments will not work.");
        } else {
            Stripe.apiKey = apiKey;
        }
    }

    /**
     * Create a Stripe Checkout Session for the given amount and open the payment page.
     *
     * @param amountDollars The amount in dollars (e.g., 50.00)
     * @param userEmail     The customer's email for pre-filling the Checkout form
     * @return A CompletableFuture that completes with true if payment succeeded, false if cancelled/timed out.
     */
    public CompletableFuture<Boolean> createCheckoutAndWaitForPayment(double amountDollars, String userEmail) {
        CompletableFuture<Boolean> paymentResult = new CompletableFuture<>();

        try {
            // Amount in cents for Stripe
            long amountCents = Math.round(amountDollars * 100);

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setCustomerEmail(userEmail)
                    .setSuccessUrl(SUCCESS_URL + "?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(CANCEL_URL)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount(amountCents)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("TravelXP Balance Recharge")
                                                                    .setDescription(String.format("Add $%.2f to your TravelXP wallet", amountDollars))
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);
            String checkoutUrl = session.getUrl();

            // Start local HTTP server to catch the redirect
            HttpServer server = HttpServer.create(new InetSocketAddress(CALLBACK_PORT), 0);

            server.createContext(SUCCESS_PATH, exchange -> {
                String html = buildResponseHtml(true);
                byte[] response = html.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
                paymentResult.complete(true);
                // Schedule server stop on a separate thread
                new Thread(() -> { try { Thread.sleep(500); } catch (InterruptedException ignored) {} server.stop(0); }).start();
            });

            server.createContext(CANCEL_PATH, exchange -> {
                String html = buildResponseHtml(false);
                byte[] response = html.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
                paymentResult.complete(false);
                new Thread(() -> { try { Thread.sleep(500); } catch (InterruptedException ignored) {} server.stop(0); }).start();
            });

            server.setExecutor(null);
            server.start();

            // Open browser to Stripe Checkout page
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(checkoutUrl));
            } else {
                // Fallback: try to open with cmd
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", checkoutUrl});
            }

            // Timeout: auto-cancel after 10 minutes
            CompletableFuture.delayedExecutor(10, TimeUnit.MINUTES).execute(() -> {
                if (!paymentResult.isDone()) {
                    paymentResult.complete(false);
                    server.stop(0);
                }
            });

        } catch (StripeException e) {
            System.err.println("Stripe API error: " + e.getMessage());
            paymentResult.completeExceptionally(e);
        } catch (IOException e) {
            System.err.println("Failed to start callback server: " + e.getMessage());
            paymentResult.completeExceptionally(e);
        }

        return paymentResult;
    }

    /**
     * Check if the Stripe API key is configured.
     */
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    private String buildResponseHtml(boolean success) {
        String color = success ? "#27ae60" : "#e74c3c";
        String icon = success ? "&#10004;" : "&#10006;";
        String title = success ? "Payment Successful!" : "Payment Cancelled";
        String message = success
                ? "Your TravelXP balance has been updated. You can close this tab and return to the app."
                : "The payment was cancelled. You can close this tab and try again in the app.";

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>TravelXP - %s</title>
                    <style>
                        body {
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                            display: flex; justify-content: center; align-items: center;
                            min-height: 100vh; margin: 0;
                            background: linear-gradient(135deg, #0a1628 0%%, #1a2d4a 100%%);
                            color: white;
                        }
                        .container {
                            text-align: center; padding: 60px;
                            background: rgba(255,255,255,0.08);
                            border-radius: 24px; backdrop-filter: blur(20px);
                            border: 1px solid rgba(255,255,255,0.1);
                            max-width: 500px;
                        }
                        .icon { font-size: 72px; color: %s; margin-bottom: 20px; }
                        h1 { font-size: 28px; margin-bottom: 15px; }
                        p { font-size: 16px; opacity: 0.8; line-height: 1.6; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="icon">%s</div>
                        <h1>%s</h1>
                        <p>%s</p>
                    </div>
                </body>
                </html>
                """.formatted(title, color, icon, title, message);
    }
}
