package com.travelxp.service;

import com.travelxp.model.Activity;
import com.travelxp.model.Booking;
import com.travelxp.model.Trip;
import com.travelxp.model.TripBooking;
import com.travelxp.model.TripMilestone;
import com.travelxp.model.User;
import com.travelxp.repository.ActivityRepository;
import com.travelxp.repository.BookingRepository;
import com.travelxp.repository.TripBookingRepository;
import com.travelxp.repository.TripMilestoneRepository;
import com.travelxp.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TripBookingRepository tripBookingRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private TripMilestoneRepository tripMilestoneRepository;

    @Autowired
    private GamificationService gamificationService;

    public List<Trip> getTripsForUser(User user) {
        return tripRepository.findByUserOrderByStartDateAsc(user);
    }

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<TripMilestone> getAllMilestones() {
        return tripMilestoneRepository.findAll();
    }

    public Trip getTripForUser(Long tripId, User user) {
        return tripRepository.findByIdAndUser(tripId, user)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found"));
    }

    @Transactional
    public Trip createTrip(User user, String name, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        Trip trip = new Trip();
        trip.setUser(user);
        trip.setTripName(name);
        trip.setStartDate(startDate);
        trip.setEndDate(endDate);
        trip.setStatus("PLANNED");
        trip.setTotalXpEarned(0);
        return tripRepository.save(trip);
    }

    @Transactional
    public Trip updateTrip(Trip trip) {
        return tripRepository.save(trip);
    }

    @Transactional
    public void deleteTrip(Long tripId) {
        tripRepository.deleteById(tripId);
    }

    @Transactional
    public Trip addBookingToTrip(Long tripId, Long bookingId, User user) {
        Trip trip = getTripForUser(tripId, user);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getGuest().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only add your own bookings to a trip");
        }

        tripBookingRepository.findByTripAndBooking(trip, booking)
                .ifPresent(tb -> { throw new IllegalStateException("Booking already in trip"); });

        TripBooking tripBooking = new TripBooking();
        tripBooking.setTrip(trip);
        tripBooking.setBooking(booking);
        tripBookingRepository.save(tripBooking);
        return trip;
    }

    @Transactional
    public Activity addActivity(Long tripId, User user, String title, String description,
                                LocalDate date, String location, BigDecimal cost, int xpReward) {
        Trip trip = getTripForUser(tripId, user);

        Activity activity = new Activity();
        activity.setTrip(trip);
        activity.setTitle(title);
        activity.setDescription(description);
        activity.setActivityDate(date);
        activity.setLocation(location);
        activity.setCost(cost == null ? BigDecimal.ZERO : cost);
        activity.setXpReward(xpReward);

        Activity saved = activityRepository.save(activity);
        awardTripXp(user, trip, xpReward, "Trip activity: " + title);
        return saved;
    }

    @Transactional
    public Activity updateActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    @Transactional
    public void deleteActivity(Long activityId) {
        activityRepository.deleteById(activityId);
    }

    @Transactional
    public TripMilestone addMilestone(Long tripId, User user, String title, String description, int xpReward) {
        Trip trip = getTripForUser(tripId, user);

        TripMilestone milestone = new TripMilestone();
        milestone.setTrip(trip);
        milestone.setTitle(title);
        milestone.setDescription(description);
        milestone.setXpReward(xpReward);
        milestone.setCompleted(false);

        return tripMilestoneRepository.save(milestone);
    }

    @Transactional
    public TripMilestone updateMilestone(TripMilestone milestone) {
        return tripMilestoneRepository.save(milestone);
    }

    @Transactional
    public void deleteMilestone(Long milestoneId) {
        tripMilestoneRepository.deleteById(milestoneId);
    }

    @Transactional
    public TripMilestone completeMilestone(Long tripId, Long milestoneId, User user) {
        Trip trip = getTripForUser(tripId, user);
        TripMilestone milestone = tripMilestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new IllegalArgumentException("Milestone not found"));

        if (!milestone.getTrip().getId().equals(trip.getId())) {
            throw new IllegalArgumentException("Milestone does not belong to this trip");
        }

        if (Boolean.TRUE.equals(milestone.getCompleted())) {
            return milestone;
        }

        milestone.markCompleted();
        TripMilestone saved = tripMilestoneRepository.save(milestone);
        awardTripXp(user, trip, milestone.getXpReward(), "Trip milestone: " + milestone.getTitle());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Activity> getActivities(Long tripId, User user) {
        Trip trip = getTripForUser(tripId, user);
        return activityRepository.findByTripOrderByActivityDateAsc(trip);
    }

    @Transactional(readOnly = true)
    public List<TripMilestone> getMilestones(Long tripId, User user) {
        Trip trip = getTripForUser(tripId, user);
        return tripMilestoneRepository.findByTripOrderByCreatedAtAsc(trip);
    }

    @Transactional(readOnly = true)
    public List<TripBooking> getTripBookings(Long tripId, User user) {
        Trip trip = getTripForUser(tripId, user);
        return tripBookingRepository.findWithBookingAndPropertyByTrip(trip);
    }

    private void awardTripXp(User user, Trip trip, int xpReward, String reason) {
        if (xpReward <= 0) {
            return;
        }
        trip.addXp(xpReward);
        tripRepository.save(trip);
        gamificationService.awardExperiencePoints(user, xpReward, reason);
    }
}
