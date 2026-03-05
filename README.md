<p align="center">
  <img src="src/main/resources/image.png" alt="TravelXP Logo" width="120"/>
</p>

<h1 align="center">TravelXP — Travel Like a Pro</h1>

<p align="center">
  A feature-rich JavaFX desktop travel platform for browsing properties, planning trips, booking accommodations, and engaging with a vibrant travel community — powered by AI, real-time maps, and gamification.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-25-orange?logo=openjdk&logoColor=white" alt="Java 25"/>
  <img src="https://img.shields.io/badge/JavaFX-25-blue?logo=java&logoColor=white" alt="JavaFX 25"/>
  <img src="https://img.shields.io/badge/Maven-3.x-C71A36?logo=apachemaven&logoColor=white" alt="Maven"/>
  <img src="https://img.shields.io/badge/MySQL-8.x-4479A1?logo=mysql&logoColor=white" alt="MySQL"/>
  <img src="https://img.shields.io/badge/Stripe-Payments-635BFF?logo=stripe&logoColor=white" alt="Stripe"/>
  <img src="https://img.shields.io/badge/Gemini_AI-2.5_Flash-4285F4?logo=googlegemini&logoColor=white" alt="Gemini AI"/>
  <img src="https://img.shields.io/badge/OpenCV-Face_ID-5C3EE8?logo=opencv&logoColor=white" alt="OpenCV"/>
  <img src="https://img.shields.io/badge/License-MIT-green" alt="License"/>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Controllers-20-blueviolet" alt="20 Controllers"/>
  <img src="https://img.shields.io/badge/Services-24-ff69b4" alt="24 Services"/>
  <img src="https://img.shields.io/badge/Models-14-yellow" alt="14 Models"/>
  <img src="https://img.shields.io/badge/Repositories-8-cyan" alt="8 Repositories"/>
  <img src="https://img.shields.io/badge/APIs-7_Integrated-success" alt="7 APIs"/>
</p>

---

## Table of Contents

- [Overview](#overview)
- [Highlights at a Glance](#highlights-at-a-glance)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [API Integrations](#api-integrations)
- [Database](#database)
- [Screenshots](#screenshots)
- [Contributors](#contributors)
- [Quick Start Cheat Sheet](#quick-start-cheat-sheet)

---

## Overview

**TravelXP** is a comprehensive desktop travel management application built with **JavaFX** and **Maven**. It provides a complete ecosystem for travelers to discover accommodations, plan multi-destination trips, manage bookings with dynamic pricing, and interact with an AI-powered assistant — all within a modern, themeable UI.

The platform supports two roles — **User** and **Admin** — each with tailored dashboards and capabilities.

---

## Highlights at a Glance

<table>
<tr>
<td align="center" width="25%">

### 🔐 3-Layer Auth
Password + Face ID + TOTP 2FA

</td>
<td align="center" width="25%">

### 🤖 AI Assistant
Gemini 2.5 Flash Chatbot

</td>
<td align="center" width="25%">

### 💰 Smart Pricing
Season · Demand · Discounts

</td>
<td align="center" width="25%">

### 🏆 Gamification
XP · Levels · 12+ Titles

</td>
</tr>
<tr>
<td align="center" width="25%">

### 🗺️ Geolocation
Geocoding + Route Planning

</td>
<td align="center" width="25%">

### 💳 Stripe Payments
Checkout + Wallet System

</td>
<td align="center" width="25%">

### 🌍 Multi-language
Auto-detect & Translate 10 langs

</td>
<td align="center" width="25%">

### 🎨 Themes
Dark / Light with smooth transitions

</td>
</tr>
</table>

---

## Features

### Authentication & Security
- **Email/Password Login** with BCrypt-hashed credentials
- **Face ID Login** — biometric authentication using OpenCV (LBPH face recognition via webcam)
- **TOTP Two-Factor Authentication** — RFC 6238 compliant, compatible with Google Authenticator and Authy (QR code setup via ZXing)
- **Password Management** — secure password change flow
- **Session Management** — persistent user sessions across views

### Trip Planning
- Full **CRUD** for trips with origin, destination, dates, budget, and status tracking (`PLANNED` → `ONGOING` → `COMPLETED` / `CANCELLED`)
- **Trip Participation** — join or leave public trips; participant tracking
- **Activity Management** — add activities (with type, date, cost, status) linked to trips
- **Trip Milestones** — track progress landmarks within a trip
- **XP Rewards** — earn experience points for completing activities

### Property & Accommodation
- Browse and manage properties with detailed listings (type, address, city, country, bedrooms, bathrooms, max guests, price/night, images)
- **Geolocation** — automatic lat/long resolution with Nominatim (OpenStreetMap) geocoding
- **Property Recommendations** — smart suggestions highlighting properties with active offers ≥ 30% discount
- **Offers System** — time-bound discount offers on properties

### Booking Engine
- Complete booking flow with date selection, guest count, and extra services
- **Dynamic Pricing Engine** with transparent breakdowns:
  - Seasonal multipliers (Peak Summer ×1.30, Winter Holiday ×1.25, Shoulder ×1.10, Off-Peak ×0.90)
  - Demand-based pricing (high demand ×1.20, medium ×1.10)
  - Extra guest surcharges ($15/night per guest beyond 2)
  - Length-of-stay discounts (weekly 10%, monthly 20%)
- **Cancellation Policy Engine** — tiered refund system (100% within 24h, 50% if > 3 days out, 0% otherwise)
- **Email Confirmations** — automated booking, cancellation, and modification emails via SMTP

### Payments & Wallet
- **Stripe Checkout** integration for secure online payments
- **In-app Wallet** — balance management with top-up (via Stripe) and automatic deductions on booking
- **Currency Exchange** — live conversion rates from ExchangeRate-API for any ISO 4217 currency pair

### AI-Powered Features
- **Gemini AI Chat Assistant** — Google Gemini 2.5 Flash-powered chatbot with full platform knowledge, multi-turn conversation support, and contextual guidance
- **Trip AI Assistant** — generates personalized trip-specific advice based on route, dates, and budget
- **Sentiment Analysis** — automatic keyword-based scoring (POSITIVE / NEGATIVE / NEUTRAL) on user feedback

### Communication
- **Real-time Messaging** — 1-to-1 conversations between users, optionally linked to feedback items
- **Unread Indicators** — per-conversation unread message counts
- **Email Notifications** — transactional emails for key booking events

### Gamification
- **XP & Leveling System** — earn XP from trips, activities, and engagement
- **12+ Titles** — progress from *Novice* → *Explorer* → *Traveler* → *Globetrotter* → *Adventurer* → … → *Beyond Limits*
- **Level Thresholds** — L1 (0 XP), L2 (50), L3 (120), L4 (200), L5 (300), then +100/level
- **Dashboard Progress Bar** — visual rank, level, and title display

### Content Moderation
- **Profanity Filter** — regex-based blacklist with automatic masking (e.g., `f***`)
- **Grammar Checking** — LanguageTool API integration for real-time spelling and grammar correction
- **Multi-language Translation** — MyMemory Translation API with auto-detection (supports French, Spanish, Italian, German, Portuguese, Russian, Japanese, Korean, Chinese, English)
- **Feedback System** — full CRUD with profanity filtering, sentiment tagging, and duplicate detection
- **Comments** — threaded comments on feedback items

### Admin Dashboard
- **User Management** — view, search, filter, sort, create, edit, role-assign, and delete users
- **Resource Management** — admin CRUD for Properties, Offers, Bookings, Trips, Activities, and Services
- **Moderation Panel** — review and manage all user-generated feedback and comments

### UI/UX
- **Dark / Light Theme Toggle** — AtlantaFX themes (PrimerDark / PrimerLight) with smooth fade transitions
- **Responsive Full-screen Layout** — adapts to screen resolution
- **Rich Dashboard** — profile card, wallet balance, gamification rank, featured properties, and featured trips

---

## Tech Stack

<table>
<tr><th>Layer</th><th>Technology</th><th>Version</th></tr>
<tr><td>☕ <b>Language</b></td><td>Java</td><td>25</td></tr>
<tr><td>🖼️ <b>UI Framework</b></td><td>JavaFX + FXML</td><td>25</td></tr>
<tr><td>🎨 <b>UI Theme</b></td><td>AtlantaFX</td><td>2.0.1</td></tr>
<tr><td>🔧 <b>Build Tool</b></td><td>Apache Maven</td><td>3.x</td></tr>
<tr><td>🗄️ <b>Database</b></td><td>MySQL</td><td>8.x</td></tr>
<tr><td>🔌 <b>DB Connector</b></td><td>MySQL Connector/J</td><td>8.3.0</td></tr>
<tr><td>🔐 <b>Authentication</b></td><td>jBCrypt, OpenCV/JavaCV, ZXing</td><td>0.4 · 1.5.10 · 3.5.3</td></tr>
<tr><td>💳 <b>Payments</b></td><td>Stripe Java SDK</td><td>24.18.0</td></tr>
<tr><td>🤖 <b>AI</b></td><td>Google Gemini 2.5 Flash</td><td>REST API</td></tr>
<tr><td>📧 <b>Email</b></td><td>JavaMail (javax.mail)</td><td>1.6.2</td></tr>
<tr><td>📋 <b>JSON</b></td><td>org.json, Gson</td><td>20231013 · 2.10.1</td></tr>
<tr><td>📄 <b>PDF</b></td><td>iTextPDF</td><td>—</td></tr>
<tr><td>📱 <b>SMS</b></td><td>Twilio</td><td>—</td></tr>
<tr><td>🌐 <b>HTTP</b></td><td>java.net.http, OkHttp</td><td>—</td></tr>
</table>

---

## Architecture

The project follows a **layered MVC architecture** with a clean separation of concerns:

```
com.travelxp
├── ai/                  # AI service layer (Gemini, Trip AI)
├── controllers/         # JavaFX FXML controllers (20 controllers)
├── models/              # Data models / POJOs (14 models)
├── repositories/        # Data access layer (8 repositories)
├── services/            # Business logic layer (24 services)
└── utils/               # Utilities (DB, profanity filter, sentiment, etc.)
```

**Design Patterns Used:**
- **Repository Pattern** — clean data access abstraction
- **Service Layer** — encapsulated business logic
- **MVC** — Model-View-Controller via JavaFX + FXML
- **Singleton** — database connection (`MyDB`), user session (`UserSession`)
- **Strategy** — dynamic pricing and cancellation policy engines

### Application Flow

```mermaid
flowchart LR
    A[🚀 Launch App] --> B{Authenticate}
    B -->|Password| C[Login]
    B -->|Face ID| C
    B -->|New User| D[Register]
    D --> C
    C --> E[🏠 Dashboard]
    E --> F[🗺️ Trips]
    E --> G[🏡 Properties]
    E --> H[🤖 AI Chat]
    E --> I[👤 Profile]
    F --> J[📋 Activities]
    G --> K[📅 Booking]
    K --> L{Payment}
    L -->|Wallet| M[✅ Confirmed]
    L -->|Stripe| M
    M --> N[📧 Email Sent]
    J --> O[⭐ Earn XP]
    O --> P[🏆 Level Up!]
```

### Authentication Flow

```mermaid
flowchart TD
    Start([User Opens App]) --> Login[Login Screen]
    Login --> Cred{Auth Method?}
    Cred -->|Email + Password| Verify[Verify BCrypt Hash]
    Cred -->|Face ID| Cam[📷 Webcam Capture]
    Cam --> LBPH[LBPH Recognition]
    LBPH -->|Confidence ≥ 85| TwoFA
    LBPH -->|Confidence < 85| Fail[❌ Not Recognized]
    Verify -->|Valid| TwoFA{TOTP Enabled?}
    Verify -->|Invalid| Fail2[❌ Wrong Credentials]
    TwoFA -->|Yes| TOTP[Enter 6-digit Code]
    TwoFA -->|No| Success
    TOTP -->|Valid ±1 window| Success[✅ Dashboard]
    TOTP -->|Invalid| Fail3[❌ Invalid Code]

    style Success fill:#22c55e,color:#fff
    style Fail fill:#ef4444,color:#fff
    style Fail2 fill:#ef4444,color:#fff
    style Fail3 fill:#ef4444,color:#fff
```

### Dynamic Pricing Engine

```mermaid
flowchart TD
    Base["💲 Base Price / Night"] --> Season{Season?}
    Season -->|Jun-Aug| Peak["×1.30 Peak Summer"]
    Season -->|Dec-Jan| Winter["×1.25 Winter Holiday"]
    Season -->|Mar-May, Sep| Shoulder["×1.10 Shoulder"]
    Season -->|Other| Off["×0.90 Off-Peak"]
    
    Peak & Winter & Shoulder & Off --> Demand{Demand Level?}
    Demand -->|≥10 bookings| High["×1.20 High Demand"]
    Demand -->|≥5 bookings| Med["×1.10 Medium Demand"]
    Demand -->|< 5| Normal["×1.00 Normal"]
    
    High & Med & Normal --> Guests{Extra Guests?}
    Guests -->|> 2 guests| Extra["+$15/night per extra"]
    Guests -->|≤ 2| NoExtra[No surcharge]
    
    Extra & NoExtra --> Stay{Stay Length?}
    Stay -->|28+ nights| Monthly["-20% Monthly"]
    Stay -->|7+ nights| Weekly["-10% Weekly"]
    Stay -->|< 7| NoDisc[No discount]
    
    Monthly & Weekly & NoDisc --> Total["🧾 Final Price + Services"]

    style Total fill:#22c55e,color:#fff,stroke:#16a34a
    style Base fill:#3b82f6,color:#fff
```

### Entity Relationship Overview

```mermaid
erDiagram
    USER ||--o{ TRIP : creates
    USER ||--o{ BOOKING : makes
    USER ||--o{ FEEDBACK : writes
    USER ||--o{ MESSAGE : sends
    USER ||--o{ GAMIFICATION : earns
    
    TRIP ||--o{ ACTIVITY : contains
    TRIP ||--o{ TRIP_MILESTONE : tracks
    TRIP ||--o{ BOOKING : "linked to"
    
    PROPERTY ||--o{ BOOKING : "booked at"
    PROPERTY ||--o{ OFFER : has
    
    BOOKING }o--o{ SERVICE : includes
    
    FEEDBACK ||--o{ COMMENT : receives
    
    CONVERSATION ||--o{ MESSAGE : contains
    USER ||--o{ CONVERSATION : participates

    USER {
        int id PK
        string username
        string email
        string password_hash
        string role
        float balance
        int xp
        boolean totp_enabled
    }
    TRIP {
        int id PK
        string name
        string origin
        string destination
        date start_date
        date end_date
        float budget
        string status
    }
    PROPERTY {
        int id PK
        string title
        string city
        string country
        float price_per_night
        float latitude
        float longitude
    }
    BOOKING {
        int id PK
        date booking_date
        int duration
        float total_price
        string status
        int num_guests
    }
```

### Gamification Progression

```mermaid
gantt
    title XP Level Progression
    dateFormat X
    axisFormat %s XP

    section Levels
    L1 Novice           :done, 0, 50
    L2 Explorer          :done, 50, 120
    L3 Traveler          :active, 120, 200
    L4 Globetrotter      :200, 300
    L5 Adventurer        :300, 400
    L6 Voyager           :400, 500
    L7 Wanderer          :500, 600
    L8 Nomad             :600, 700
    L9 Pioneer           :700, 800
    L10 Trailblazer      :800, 900
```

---

## Prerequisites

- **Java JDK 25** (or compatible)
- **Apache Maven 3.x**
- **MySQL 8.x** server
- **Webcam** (optional — required for Face ID feature)

---

## Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-org/Esprit-PIDEV-3A1-2526-Travelxp.git
   cd Esprit-PIDEV-3A1-2526-Travelxp
   ```

2. **Set up the database**
   ```bash
   mysql -u root -p < travelxp.sql
   ```
   For incremental schema updates, apply the migration files in `database lifeline/` in order (see [Database](#database) section).

3. **Install dependencies**
   ```bash
   mvn clean install
   ```

4. **Configure the application** (see [Configuration](#configuration))

5. **Run the application**
   ```bash
   mvn javafx:run
   ```

---

## Configuration

Edit `src/main/resources/db.properties` to configure the application:

```properties
# Database
db.url=jdbc:mysql://localhost:3306/travelxp?useSSL=false&serverTimezone=UTC
db.user=root
db.password=

# Stripe (required for payments)
stripe.secret.key=sk_test_...

# Google Gemini AI (required for AI chat)
gemini.api.key=your_gemini_api_key

# SMTP Email (required for email notifications)
mail.smtp.host=smtp.gmail.com
mail.smtp.port=587
mail.smtp.user=your_email@gmail.com
mail.smtp.password=your_app_password
mail.from=your_email@gmail.com
```

> **Note:** For Gmail SMTP, use an [App Password](https://support.google.com/accounts/answer/185833) rather than your account password.

---

## Usage

```mermaid
journey
    title User Journey in TravelXP
    section Getting Started
      Register an account: 5: User
      Set up Face ID or TOTP: 4: User
      Explore the dashboard: 5: User
    section Planning a Trip
      Create a new trip: 5: User
      Add activities: 4: User
      Invite participants: 4: User
    section Booking
      Browse properties: 5: User
      Review dynamic pricing: 3: User
      Confirm & pay: 5: User, Stripe
      Receive email confirmation: 5: System
    section Engagement
      Chat with Gemini AI: 5: User, AI
      Complete activities for XP: 4: User
      Level up & earn titles: 5: User
      Leave feedback: 4: User
```

### Step-by-Step

1. **Register** a new account or **log in** with existing credentials
2. **Set up Face ID** or **TOTP 2FA** from your profile for enhanced security
3. **Browse properties** and explore featured listings on the dashboard
4. **Plan trips** — create trips, add activities, and invite participants
5. **Book accommodations** — select dates, review dynamic pricing breakdown, and confirm
6. **Recharge wallet** via Stripe to fund bookings
7. **Chat with Gemini AI** for personalized travel guidance
8. **Earn XP** by completing trips and activities to climb the leaderboard
9. **Leave feedback** and engage with the community through comments and messaging

---

## Project Structure

```
Esprit-PIDEV-3A1-2526-Travelxp/
├── pom.xml                          # Maven project configuration
├── travelxp.sql                     # Main database schema
├── database lifeline/               # Incremental migration scripts
│   ├── setup.sql
│   ├── migration.sql
│   ├── role_migration.sql
│   ├── face_id_migration.sql
│   ├── totp_migration.sql
│   ├── booking_services_migration.sql
│   ├── dynamic_pricing_migration.sql
│   ├── feedback_migration.sql
│   ├── messaging_migration.sql
│   ├── property_geocoding_migration.sql
│   ├── trips_migration.sql
│   └── ...
├── faces/                           # Face ID training data & model
│   ├── face_model.yml
│   └── haarcascade_frontalface_default.xml
├── uploads/                         # User-uploaded images
└── src/
    └── main/
        ├── java/
        │   ├── module-info.java
        │   └── com/travelxp/
        │       ├── Main.java                  # Application entry point
        │       ├── UserSession.java           # Session management
        │       ├── ai/                        # AI services
        │       ├── controllers/               # 20 FXML controllers
        │       ├── models/                    # 14 data models
        │       ├── repositories/              # 8 data repositories
        │       ├── services/                  # 24 business services
        │       └── utils/                     # Utility classes
        └── resources/
            ├── db.properties                  # App configuration
            ├── style.css                      # Global stylesheet
            ├── com/travelxp/views/            # FXML view files
            └── icons/                         # UI icons
```

---

## API Integrations

| API | Purpose | Auth |
|-----|---------|------|
| [Google Gemini 2.5 Flash](https://ai.google.dev/) | AI chatbot assistant | API Key |
| [Stripe Checkout](https://stripe.com/docs/checkout) | Online payments & wallet recharge | Secret Key |
| [ExchangeRate-API](https://open.er-api.com/) | Live currency exchange rates | None |
| [Nominatim (OpenStreetMap)](https://nominatim.org/) | Forward & reverse geocoding | None |
| [OSRM](http://router.project-osrm.org/) | Driving routes & turn-by-turn directions | None |
| [LanguageTool](https://languagetool.org/http-api/) | Grammar & spelling checking | None |
| [MyMemory Translation](https://mymemory.translated.net/) | Multi-language text translation | None |

---

## Database

The application uses a **MySQL** database named `travelxp`. The main schema is defined in `travelxp.sql`.

Incremental migrations are stored in the `database lifeline/` directory and should be applied in chronological order for existing installations. Key tables include:

| Table | Description |
|-------|-------------|
| `users` | User accounts, roles, wallet balance, XP, TOTP & Face ID data |
| `trips` | Trip itineraries with origin, destination, dates, status |
| `activities` | Trip-linked activities with type, date, cost |
| `properties` | Accommodation listings with geolocation |
| `bookings` | Reservation records linking users, properties, and trips |
| `booking_services` | Many-to-many junction for extra booking services |
| `services` | Available add-on services |
| `offers` | Time-bound discount offers on properties |
| `feedback` | User feedback with sentiment tags |
| `comments` | Threaded comments on feedback |
| `conversations` | Messaging conversations |
| `messages` | Individual chat messages |
| `trip_milestones` | Progress milestones within trips |
| `gamification` | XP, level, and title tracking |

### Database Schema Map

```mermaid
flowchart TD
    subgraph "👤 User Domain"
        U[(users)]
        G[(gamification)]
    end
    
    subgraph "✈️ Trip Domain"
        T[(trips)]
        A[(activities)]
        TM[(trip_milestones)]
    end
    
    subgraph "🏡 Property Domain"
        P[(properties)]
        O[(offers)]
    end
    
    subgraph "📅 Booking Domain"
        B[(bookings)]
        BS[(booking_services)]
        S[(services)]
    end
    
    subgraph "💬 Social Domain"
        F[(feedback)]
        C[(comments)]
        CV[(conversations)]
        M[(messages)]
    end
    
    U --> G
    U --> T
    U --> B
    U --> F
    U --> CV
    T --> A
    T --> TM
    T --> B
    P --> B
    P --> O
    B --> BS
    BS --> S
    F --> C
    CV --> M

    style U fill:#3b82f6,color:#fff
    style T fill:#f59e0b,color:#fff
    style P fill:#10b981,color:#fff
    style B fill:#8b5cf6,color:#fff
    style F fill:#ef4444,color:#fff
```

---

## Screenshots

> 📸 _Add your application screenshots below. A recommended layout is provided:_

<!--
### 🔐 Login & Authentication
| Login | Face ID | TOTP Setup |
|:---:|:---:|:---:|
| ![Login](screenshots/login.png) | ![Face ID](screenshots/faceid.png) | ![TOTP](screenshots/totp.png) |

### 🏠 Dashboard & Navigation
| Dashboard | Dark Mode | Profile |
|:---:|:---:|:---:|
| ![Dashboard](screenshots/dashboard.png) | ![Dark](screenshots/dark-mode.png) | ![Profile](screenshots/profile.png) |

### ✈️ Core Features
| Trip Planning | Booking | AI Chat |
|:---:|:---:|:---:|
| ![Trips](screenshots/trips.png) | ![Booking](screenshots/booking.png) | ![AI](screenshots/ai-chat.png) |

### 🛠️ Admin Panel
| User Management | Properties | Moderation |
|:---:|:---:|:---:|
| ![Admin](screenshots/admin.png) | ![Properties](screenshots/admin-properties.png) | ![Moderation](screenshots/moderation.png) |
-->

---

## Contributors

This project was developed as part of the **PIDEV 3A1** coursework at **[ESPRIT](https://esprit.tn/)** (2025–2026).

<table>
<tr>
<td align="center">
<a href="https://github.com/Apolake">
<img src="https://github.com/Apolake.png" width="80px;" alt="Yassine Raddadi"/><br />
<sub><b>Yassine Raddadi</b></sub>
</a>
</td>
<td align="center">
<a href="https://github.com/Dhia-Raddaoui">
<img src="https://github.com/Dhia-Raddaoui.png" width="80px;" alt="Dhia Raddaoui"/><br />
<sub><b>Dhia Raddaoui</b></sub>
</a>
</td>
<td align="center">
<a href="https://github.com/navTace">
<img src="https://github.com/navTace.png" width="80px;" alt="Anas Nafti"/><br />
<sub><b>Anas Nafti</b></sub>
</a>
</td>
<td align="center">
<a href="https://github.com/ysfltm">
<img src="https://github.com/ysfltm.png" width="80px;" alt="Youssef Litaiem"/><br />
<sub><b>Youssef Litaiem</b></sub>
</a>
</td>
<td align="center">
<a href="https://github.com/omarhlal49">
<img src="https://github.com/omarhlal49.png" width="80px;" alt="Omar Ehlal"/><br />
<sub><b>Omar Ehlal</b></sub>
</a>
</td>
</tr>
</table>

---

## Quick Start Cheat Sheet

```bash
# 1. Clone & enter
git clone https://github.com/your-org/Esprit-PIDEV-3A1-2526-Travelxp.git
cd Esprit-PIDEV-3A1-2526-Travelxp

# 2. Database
mysql -u root -p < travelxp.sql

# 3. Configure (edit db.properties with your keys)
notepad src/main/resources/db.properties

# 4. Build & Run
mvn clean javafx:run
```

---

<p align="center">
  <img src="https://img.shields.io/badge/Made_with-☕_Java-orange?style=for-the-badge" alt="Made with Java"/>
  <img src="https://img.shields.io/badge/Powered_by-🤖_Gemini_AI-4285F4?style=for-the-badge" alt="Powered by Gemini"/>
  <img src="https://img.shields.io/badge/Built_at-🎓_ESPRIT-E4002B?style=for-the-badge" alt="Built at ESPRIT"/>
</p>

<p align="center">
  Built with ❤️ at <strong>ESPRIT</strong> — 2025/2026
</p>
