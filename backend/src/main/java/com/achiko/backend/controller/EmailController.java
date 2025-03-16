package com.achiko.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.achiko.backend.dto.EmailAuthDTO;
import com.achiko.backend.service.EmailAuthService;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/user")
@RestController
@Slf4j
public class EmailController {

    @Autowired
    private EmailAuthService emailAuthService;

    // 텍스트 이메일 전송 API
    @PostMapping("/sendEmail")
    public void sendEmail(@ModelAttribute EmailAuthDTO emailAuthDTO){
    	emailAuthService.sendAuthCode(emailAuthDTO);
    }
    
 // 2. 이메일 인증번호 확인
    @PostMapping("/verifyAuthCode")
    public int verifyAuthCode(@RequestParam(name="email") String email, @RequestParam(name="chkAuthCode") String chkAuthCode) {
        int isVerified = emailAuthService.verifyAuthCode(email, chkAuthCode);
        
        return isVerified;
    }
}