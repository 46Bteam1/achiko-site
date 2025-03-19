package com.achiko.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_review")
public class UserReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "reviewed_user_id", referencedColumnName = "user_id") // Ensure correct join column
    private UsersEntity reviewedUser;
    
    @Column(name = "cleanliness_rating")
    private Integer cleanlinessRating;

    @Column(name = "trust_rating")
    private Integer trustRating;

    @Column(name = "communication_rating")
    private Integer communicationRating;

    @Column(name = "manner_rating")
    private Integer mannerRating;
    
    @Column(columnDefinition = "TEXT")
    private String comment;
}