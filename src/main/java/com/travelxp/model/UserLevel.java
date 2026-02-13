package com.travelxp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_levels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "level_name", nullable = false, length = 50)
    private String levelName;

    @Column(name = "level_number", unique = true, nullable = false)
    private Integer levelNumber;

    @Column(name = "xp_required", nullable = false)
    private Integer xpRequired;

    @Column(name = "badge_icon")
    private String badgeIcon;

    @Column(columnDefinition = "TEXT")
    private String benefits;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
