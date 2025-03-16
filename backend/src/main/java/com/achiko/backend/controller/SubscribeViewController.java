package com.achiko.backend.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.achiko.backend.dto.LoginUserDetails;
import com.achiko.backend.service.SubscribeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "ViewSubscribe", description = "ViewSubscribe API")
@Controller
@RequiredArgsConstructor
@RequestMapping("/viewSubscribe")
public class SubscribeViewController {
	
	private final SubscribeService subscribeService;

	@Operation(summary = "구독 결제 페이지 조회", description = "subscribePage를 반환합니다.")
	@GetMapping("/subscribePage")
	public String subscribePage(@AuthenticationPrincipal LoginUserDetails loginUser, Model model) {
		
		if (loginUser != null) {
			String updatedIsSubscribed = subscribeService.getUserSubscriptionStatus(loginUser.getUserId());
			model.addAttribute("loginId", loginUser.getUsername());
			model.addAttribute("userId", loginUser.getUserId());
			model.addAttribute("email", loginUser.getEmail());
			model.addAttribute("isSubscribed", updatedIsSubscribed);
		}
		return "subscribe/subscribePage";
	}

	@Operation(summary = "결제 성공 페이지", description = "결제 성공 시 리디렉트될 페이지")
	@GetMapping("/paymentSuccess")
	public String paymentSuccessPage() {
		return "subscribe/paymentSuccess";
	}

}
