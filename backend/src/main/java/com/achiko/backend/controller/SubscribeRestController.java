package com.achiko.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.achiko.backend.dto.LoginUserDetails;
import com.achiko.backend.dto.PrincipalDetails;
import com.achiko.backend.service.SubscribeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscribe")
public class SubscribeRestController {

	private final SubscribeService subscribeService;

	@Value("${BOOTPAY_APPLICATION_ID}")
	private String bootpayApplicationId;

	@GetMapping("/config")
	public ResponseEntity<?> getBootpayConfig() {
		return ResponseEntity.ok(Map.of("bootpayApplicationId", bootpayApplicationId));
	}

	/**
	 * Bootpay Access Token 발급 API
	 */
	@GetMapping("/token")
	public ResponseEntity<?> getAccessToken() {
		try {
			String accessToken = subscribeService.getAccessToken();
			return ResponseEntity.ok(Map.of("accessToken", accessToken));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("토큰 발급 실패: " + e.getMessage());
		}
	}

	/**
	 * 결제 성공 후 receiptId 변경 API
	 */
	@PostMapping("/updateSubscription")
	public ResponseEntity<Map<String, String>> updateSubscription(@AuthenticationPrincipal PrincipalDetails loginUser,
			@RequestParam(name = "receiptId") String receiptId) {
		if (loginUser == null) {
			return ResponseEntity.ok(Map.of("redirectUrl", "/user/login", "message", "로그인이 필요합니다."));
		}

		try {
			subscribeService.updateSubscriptionStatus(loginUser.getUserId(), receiptId);
			return ResponseEntity
					.ok(Map.of("message", "구독 상태가 업데이트되었습니다.", "redirectUrl", "/"));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("message", "구독 상태 업데이트 실패: " + e.getMessage()));
		}
	}

	// Bootpay 결제 취소 API 호출
	@PostMapping("/cancelSubscription")
	public ResponseEntity<Map<String, Object>> cancelSubscription(@RequestBody Map<String, Long> request) {
		Long userId = request.get("userId");

		if (userId == null) {
			return ResponseEntity.badRequest().body(Map.of("message", "userId가 필요합니다."));
		}

		try {
			HashMap<String, Object> response = subscribeService.cancelSubscription(userId);
			return ResponseEntity.ok(Map.of("message", "결제 취소 성공", "redirectUrl", "/viewSubscribe/subscribePage"));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("message", "결제 취소 실패: " + e.getMessage()));
		}
	}

}
