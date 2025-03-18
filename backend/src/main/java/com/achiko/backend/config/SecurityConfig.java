package com.achiko.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.achiko.backend.dto.PrincipalDetails;
import com.achiko.backend.handler.LoginFailureHandler;
import com.achiko.backend.handler.LoginSuccessHandler;
import com.achiko.backend.service.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
	
	private final LoginFailureHandler loginFailureHandler;		// 로그인 실패 처리 Handler
	private final CustomOAuth2UserService customOAuth2UserService;
	private final LoginSuccessHandler loginSuccessHandler;
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests((auth) -> auth
						.requestMatchers(
								"/"
								, "/api/location/**"
								, "/share/selectAll"
								, "/api/search/**"
								, "/oauth2/**"
								, "/torii/**"
								, "/user/**"
								, "/user/verifyAuthCode"
								, "/user/findLoginIdResult"
								, "/images/**"
								, "/css/**"
								, "/js/**")
						.permitAll()
						.requestMatchers("/admin").hasRole("ADMIN")
						.requestMatchers("/user/mypage").hasAnyRole("ADMIN", "USER")
						.anyRequest().authenticated());
						
		// Custom Login 설정
		http
			.formLogin((auth) -> auth
					.loginPage("/user/login")
					.loginProcessingUrl("/user/loginProc")
					.usernameParameter("loginId")
					.passwordParameter("password")
					.failureHandler(loginFailureHandler)		// FailureHandler가 있으면 이 코드는 없어야함
					.defaultSuccessUrl("/", true)
					.successHandler(loginSuccessHandler)
					.permitAll());

		// logout 설정
		http
			.logout((auth) -> auth
					.logoutUrl("/user/logout")
					.logoutSuccessUrl("/")
					.invalidateHttpSession(true)
					.clearAuthentication(true));

		// POST 요청시 CSRF 토큰을 요청하므로 (Cross-Site Request Forgery) 비활성화(개발환경)
		http
			.csrf((auth) -> auth.disable());
		
		// 소셜로그인 설정
		http
		.oauth2Login((oauth2) -> oauth2
				.successHandler(loginSuccessHandler)
				.userInfoEndpoint((userInfoEndpointConfig) ->
				userInfoEndpointConfig.userService(customOAuth2UserService)));

		return http.build();
		
	}

	// 단방향 비밀번호 암호화
	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}