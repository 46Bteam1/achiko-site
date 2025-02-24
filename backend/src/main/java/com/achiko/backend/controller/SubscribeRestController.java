package com.achiko.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.achiko.backend.dto.LoginUserDetails;
import com.achiko.backend.service.SubscribeService;
import com.achiko.backend.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscribe")
public class SubscribeRestController {

    private final SubscribeService subscribeService;
    private final UserService userService;
    
    @Value("${BOOTPAY_APPLICATION_ID}")
    private String bootpayApplicationId;
    
    @GetMapping("/config")
    public ResponseEntity<?> getBootpayConfig() {
        return ResponseEntity.ok(Map.of("bootpayApplicationId", bootpayApplicationId));
    }

    /**
     * ✅ Bootpay Access Token 발급 API
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
     * ✅ 결제 성공 후 is_subscribed 변경 API
     */
    @PostMapping("/updateSubscription")
    public ResponseEntity<?> updateSubscription(@AuthenticationPrincipal LoginUserDetails loginUser) {
        if (loginUser == null) {
            return ResponseEntity.badRequest().body("로그인이 필요합니다.");
        }

        try {
            userService.updateSubscriptionStatus(loginUser.getUserId(), 1); // ✅ 구독 상태를 1로 변경
            return ResponseEntity.ok(Map.of("message", "구독 상태가 업데이트되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("구독 상태 업데이트 실패: " + e.getMessage());
        }
    }

    /**
     * ✅ 정기결제용 Billing Key 발급 API
     */
//    @PostMapping("/billing-key")
//    public ResponseEntity<?> getBillingKey(@RequestBody Map<String, Object> requestData) {
//        try {
//            String userId = (String) requestData.get("userId");
//            String billingKey = subscribeService.getBillingKey(userId);
//            return ResponseEntity.ok(Map.of("billingKey", billingKey));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Billing Key 발급 실패: " + e.getMessage());
//        }
//    }

    /**
     * ✅ 정기 결제 요청 (Billing Key로 결제)
     */
//    @PostMapping("/subscribe")
//    public ResponseEntity<?> requestSubscribe(@RequestBody Map<String, Object> requestData) {
//        try {
//            String billingKey = (String) requestData.get("billingKey");
//            double price = Double.parseDouble(requestData.get("price").toString());
//            String userPhone = (String) requestData.get("userPhone");
//
//            HashMap<String, Object> response = subscribeService.requestSubscribe(billingKey, price, userPhone);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("정기 결제 요청 실패: " + e.getMessage());
//        }
//    }

    /**
     * ✅ 정기 결제 예약 API (10초 후 실행)
     */
    @PostMapping("/reserve")
    public ResponseEntity<?> reserveSubscribe(@RequestBody Map<String, Object> requestData) {
        try {
            String billingKey = (String) requestData.get("billingKey");
            double price = Double.parseDouble(requestData.get("price").toString());

            HashMap<String, Object> response = subscribeService.reserveSubscribe(billingKey, price);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("정기 결제 예약 실패: " + e.getMessage());
        }
    }

    /**
     * ✅ 정기 결제 취소 API
     */
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelPayment(@RequestBody Map<String, Object> requestData) {
        try {
            String receiptId = (String) requestData.get("receiptId");
            String cancelReason = (String) requestData.get("reason");

            HashMap<String, Object> response = subscribeService.cancelPayment(receiptId, cancelReason);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("결제 취소 실패: " + e.getMessage());
        }
    }

    /**
     * ✅ Billing Key 삭제 API (정기결제 해지)
     */
    @DeleteMapping("/billing-key/{billingKey}")
    public ResponseEntity<?> deleteBillingKey(@PathVariable String billingKey) {
        try {
            HashMap<String, Object> response = subscribeService.deleteBillingKey(billingKey);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Billing Key 삭제 실패: " + e.getMessage());
        }
    }
}
