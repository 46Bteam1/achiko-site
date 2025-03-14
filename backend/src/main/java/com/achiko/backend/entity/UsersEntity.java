package com.achiko.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class UsersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;
    
    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Column(name = "is_host")
    private int isHost;
    
    @Column(name = "languages")
    private String languages;

    @Column(name = "age")
    private Integer age;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "religion")
    private String religion;

    @Column(name = "gender")
    private Integer gender;
    
    @Column(name = "profile_image")
    private String profileImage;
}
