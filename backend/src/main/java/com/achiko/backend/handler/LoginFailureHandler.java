package com.achiko.backend.handler;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, 
										HttpServletResponse response,
										AuthenticationException exception) throws IOException, ServletException {
		String errorMessage;
		if (exception instanceof BadCredentialsException) {
			errorMessage = "아이디 또는 비밀번호를 확인해 주세요.";
		} else if (exception instanceof InternalAuthenticationServiceException) {
			errorMessage = "내부적으로 발생한 시스템 문제로 인해 요청을 처리할 수 없습니다. 관리자에게 문의하세요.";
		} else if (exception instanceof UsernameNotFoundException) {
			errorMessage = "계정이 존재하지 않습니다. 회원가입 진행 후 로그인 해주세요.";
		} else if (exception instanceof AuthenticationCredentialsNotFoundException) {
			errorMessage = "인증 요청이 거부되었습니다. 관리자에게 문의하세요.";
		} else {
			errorMessage = "알 수 없는 이유로 로그인에 실패하였습니다 관리자에게 문의하세요.";
		}
		
		// UTF-8 인코딩 (한글 깨짐 방지)
        errorMessage = java.net.URLEncoder.encode(errorMessage, "UTF-8");

        // 로그인 실패 시 로그인 페이지로 이동 ("/user/login"으로 리다이렉트)
        response.sendRedirect("/user/login?error=true&exception=" + errorMessage);
	}
}

