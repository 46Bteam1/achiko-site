package com.achiko.backend.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.achiko.backend.dto.EmailAuthDTO;
import com.achiko.backend.entity.EmailAuthEntity;
import com.achiko.backend.repository.EmailAuthRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailAuthService {
    @Autowired
    private JavaMailSender mailSender;
    private final EmailAuthRepository emailAuthRepository;
    
    private String authCode;
    private String subject = "Achiko 이메일 인증입니다.";
    private String text;
    
    
    public void sendAuthCode(EmailAuthDTO emailAuthDTO) {
    	
        // 6자리 인증번호 생성
        authCode = generateAuthCode();
        emailAuthDTO.setAuthCode(authCode);

        // 만료 시간 (5분 후)
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(5);
        emailAuthDTO.setExpiredAt(expiredAt);
        
        // 인증 정보 저장
        saveAuthInfo(emailAuthDTO);
        
        // 이메일 전송
        text = "인증번호는 " + authCode + " 입니다.";
        sendEmail(emailAuthDTO.getEmail(), subject, text);
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("achiko2250@gmail.com");

        mailSender.send(message);
    }
    
    //임의의 6자리 양수를 반환
    public String generateAuthCode() {
        Random r = new Random();
        String authCode = "";
        for(int i = 0; i < 6; i++) {
        	authCode += Integer.toString(r.nextInt(10));
        }
        
        return authCode;
    }
    
    // 인증정보 저장
    @Transactional
    public void saveAuthInfo(EmailAuthDTO emailAuthDTO) {
        // 기존 인증 정보 삭제
        emailAuthRepository.findByEmail(emailAuthDTO.getEmail()).ifPresent(emailAuthRepository::delete);

        // 새로운 인증 정보 저장
        EmailAuthEntity emailAuthEntity = EmailAuthEntity.builder()
                .email(emailAuthDTO.getEmail())
                .authCode(emailAuthDTO.getAuthCode())
                .expiredAt(emailAuthDTO.getExpiredAt())
                .verified(false)
                .build();
        
        emailAuthRepository.save(emailAuthEntity);
    }
    
    // 인증번호 검증
    @Transactional
    public int verifyAuthCode(String email, String authCode) {
        Optional<EmailAuthEntity> temp = emailAuthRepository.findByEmail(email);

        if (temp.isPresent()) {
            EmailAuthEntity emailAuthEntity = temp.get();

            // 인증번호 확인
            if (emailAuthEntity.getAuthCode().equals(authCode)) {
            	emailAuthEntity.setVerified(true); // 인증 성공 시 verified=true
                emailAuthRepository.save(emailAuthEntity);
                return 2;
            }
            
            // 만료 시간 체크
            if (emailAuthEntity.getExpiredAt().isBefore(LocalDateTime.now())) {
                return 1; // 만료됨
            }
        }
        
        return 0;		// 0: 인증번호 불일치 , 1: 인증코드 만료, 2: 인증됨
    }
}
