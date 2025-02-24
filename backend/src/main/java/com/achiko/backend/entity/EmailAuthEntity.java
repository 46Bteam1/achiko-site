package com.achiko.backend.entity;

import java.time.LocalDateTime;

import com.achiko.backend.dto.EmailAuthDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "email_auth")
public class EmailAuthEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="email_auth_id")
    private Long emailAuthId; // 기본 키

    @Column(name="email", nullable = false, unique = true)
    private String email; // 이메일 주소

    @Column(name="auth_code", nullable = false)
    private String authCode; // 인증번호 (예: 6자리 숫자)

    @Column(name="expired_at", nullable = false)
    private LocalDateTime expiredAt; // 인증번호 만료 시간

    @Column(name="verified", nullable = false)
    @Builder.Default
    private boolean verified = false; // 인증 완료 여부 (기본값: false)
    
    public EmailAuthEntity toEntity(EmailAuthDTO emailAuthDTO) {
    	return EmailAuthEntity.builder()
    			.email(emailAuthDTO.getEmail())
    			.authCode(emailAuthDTO.getAuthCode())
    			.expiredAt(emailAuthDTO.getExpiredAt())
    			.build();
    }

}