package com.achiko.backend.handler;

import com.achiko.backend.dto.PrincipalDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        // ✅ 추가 정보 입력이 필요한 경우 리디렉션
        if (principalDetails.isNeedsAdditionalInfo()) {
            response.sendRedirect("/mypage/mypageView?alert=1");
        } else {
            response.sendRedirect("/");
        }
    }
}
