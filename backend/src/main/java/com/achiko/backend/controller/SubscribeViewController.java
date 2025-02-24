package com.achiko.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "ViewSubscribe", description = "ViewSubscribe API")
@Controller
@RequestMapping("/viewSubscribe")
public class SubscribeViewController {

	@Operation(summary = "구독 결제 페이지 조회", description = "subscribePage를 반환합니다.")
	@GetMapping("/subscribePage")
	public String subscribePage() {
		return "subscribe/subscribePage";
	}

	@Operation(summary = "결제 성공 페이지", description = "결제 성공 시 리디렉트될 페이지")
	@GetMapping("/paymentSuccess")
	public String paymentSuccessPage() {
		return "subscribe/paymentSuccess";
	}

}
