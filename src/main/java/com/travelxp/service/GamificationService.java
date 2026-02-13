package com.travelxp.service;

import com.travelxp.model.Achievement;
import com.travelxp.model.Property;
import com.travelxp.model.User;
import com.travelxp.model.UserAchievement;
import com.travelxp.model.UserLevel;
import com.travelxp.repository.AchievementRepository;
import com.travelxp.repository.BookingRepository;
import com.travelxp.repository.PropertyRepository;
import com.travelxp.repository.ReviewRepository;
import com.travelxp.repository.UserAchievementRepository;
import com.travelxp.repository.UserLevelRepository;
import com.travelxp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GamificationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLevelRepository userLevelRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserAchievementRepository userAchievementRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Transactional
    public void awardExperiencePoints(User user, int points, String reason) {
        user.addExperiencePoints(points);
        checkAndUpdateLevel(user);
        userRepository.save(user);
        System.out.println("Awarded " + points + " XP to " + user.getUsername() + " for: " + reason);
    }

    @Transactional
    public void checkAndUpdateLevel(User user) {
        UserLevel currentLevel = user.getLevel();
        UserLevel nextLevel = userLevelRepository
                .findTopByXpRequiredLessThanEqualOrderByXpRequiredDesc(user.getExperiencePoints())
                .orElse(currentLevel);

        if (!nextLevel.equals(currentLevel) && nextLevel.getLevelNumber() > currentLevel.getLevelNumber()) {
            user.setLevel(nextLevel);
            System.out.println("Level Up! " + user.getUsername() + " is now " + nextLevel.getLevelName());
            user.addExperiencePoints(50);
        }
    }

    @Transactional
    public void checkBookingAchievements(User user) {
        long bookingCount = bookingRepository.countByGuest(user);

        checkAndAwardAchievement(user, "First Booking", bookingCount >= 1);
        checkAndAwardAchievement(user, "Frequent Traveler", bookingCount >= 5);
        checkAndAwardAchievement(user, "Booking Expert", bookingCount >= 10);
    }

    @Transactional
    public void checkReviewAchievements(User user) {
        long reviewCount = reviewRepository.countByReviewer(user);

        checkAndAwardAchievement(user, "Review Writer", reviewCount >= 1);
        checkAndAwardAchievement(user, "Critic", reviewCount >= 10);
    }

    @Transactional
    public void checkPropertyAchievements(User user) {
        long propertyCount = propertyRepository.findByOwner(user).size();
        checkAndAwardAchievement(user, "Property Owner", propertyCount >= 1);
    }

    private void checkAndAwardAchievement(User user, String achievementName, boolean condition) {
        if (!condition) {
            return;
        }

        List<Achievement> achievements = achievementRepository.findAll();
        Achievement achievement = achievements.stream()
                .filter(a -> a.getName().equals(achievementName))
                .findFirst()
                .orElse(null);

        if (achievement != null && !userAchievementRepository.existsByUserAndAchievement(user, achievement)) {
            UserAchievement userAchievement = new UserAchievement();
            userAchievement.setUser(user);
            userAchievement.setAchievement(achievement);
            userAchievementRepository.save(userAchievement);

            awardExperiencePoints(user, achievement.getXpReward(), "Achievement: " + achievementName);

            System.out.println("Achievement Unlocked! " + user.getUsername() + " earned: " + achievementName);
        }
    }

    public List<UserAchievement> getUserAchievements(User user) {
        return userAchievementRepository.findByUser(user);
    }

    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }
}
