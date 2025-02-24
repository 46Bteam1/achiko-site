package com.achiko.backend.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.model.request.Cancel;
import kr.co.bootpay.model.request.Subscribe;
import kr.co.bootpay.model.request.SubscribeExtra;
import kr.co.bootpay.model.request.SubscribePayload;
import kr.co.bootpay.model.request.User;

@Service
public class SubscribeService {

	private final Bootpay bootpay;

	public SubscribeService(@Value("${bootpay.application-id}") String applicationId,
			@Value("${bootpay.private-key}") String privateKey) {
		this.bootpay = new Bootpay(applicationId, privateKey);
	}

	/**
	 * ✅ BootPay Access Token 발급
	 */
	public String getAccessToken() {
		try {
			HashMap<String, Object> res = bootpay.getAccessToken();
			if (res.get("error_code") == null) { // 성공 시
				return (String) res.get("access_token");
			}
			throw new RuntimeException("BootPay Access Token 요청 실패: " + res);
		} catch (Exception e) {
			throw new RuntimeException("BootPay Access Token 발급 중 오류 발생: " + e.getMessage());
		}
	}

	/**
	 * ✅ 정기 결제 (Billing Key 발급)
	 */
	public String getBillingKey(String userId, String pg) {
	    // ✅ Bootpay에 Access Token 설정
	    String accessToken = getAccessToken();
	    bootpay.setToken(accessToken);

	    Subscribe subscribe = new Subscribe();
	    subscribe.orderName = "정기결제 테스트 아이템";
	    subscribe.subscriptionId = "sub_" + System.currentTimeMillis();

	    // ✅ PG 설정을 Bootpay 요청에 포함시키기 위해 `Subscribe` 객체에서 직접 설정
	    subscribe.pg = pg; // ✅ PG 설정 (Bootpay에서 지원하는 PG 코드 사용)

	    // ✅ 샌드박스 모드 활성화
	    SubscribeExtra extra = new SubscribeExtra();
	    extra.subscribeTestPayment = 1; // ✅ 테스트 결제 활성화
	    subscribe.extra = extra;

	    subscribe.cardNo = "5570**********1074"; // ✅ 테스트 카드번호
	    subscribe.cardPw = "**"; // ✅ 카드 비밀번호 앞 2자리
	    subscribe.cardExpireYear = "**"; // ✅ 카드 유효기간 (YY)
	    subscribe.cardExpireMonth = "**"; // ✅ 카드 유효기간 (MM)
	    subscribe.cardIdentityNo = ""; // ✅ 생년월일 or 사업자번호

	    subscribe.user = new User();
	    subscribe.user.username = userId;
	    subscribe.user.phone = "01011112222";

	    try {
	        HashMap<String, Object> res = bootpay.getBillingKey(subscribe);
	        if (res != null && res.get("billing_key") != null) {
	            return (String) res.get("billing_key");
	        }
	        throw new RuntimeException("Billing Key 발급 실패: 응답이 null이거나 키가 없음");
	    } catch (Exception e) {
	        throw new RuntimeException("Billing Key 발급 중 오류 발생: " + e.getMessage());
	    }
	}

	/**
	 * ✅ 정기 결제 요청 (Billing Key로 결제)
	 */
	public HashMap<String, Object> requestSubscribe(String billingKey, double price, String userPhone, String pg) {
	    // ✅ Bootpay에 Access Token 설정
	    String accessToken = getAccessToken();
	    bootpay.setToken(accessToken);

	    SubscribePayload payload = new SubscribePayload();
	    payload.billingKey = billingKey;
	    payload.orderName = "구독 서비스 결제";
	    payload.price = price;
	    payload.orderId = "sub_" + System.currentTimeMillis();
//	    payload.pg = pg; // ✅ PG 설정 추가

	    payload.user = new User();
	    payload.user.phone = userPhone;

	    try {
	        return bootpay.requestSubscribe(payload);
	    } catch (Exception e) {
	        throw new RuntimeException("정기 결제 요청 중 오류 발생: " + e.getMessage());
	    }
	}

	/**
	 * ✅ 정기 결제 예약 (10초 뒤 실행)
	 */
	public HashMap<String, Object> reserveSubscribe(String billingKey, double price) {
		// ✅ Bootpay에 Access Token 설정
		String accessToken = getAccessToken();
		bootpay.setToken(accessToken);

		SubscribePayload payload = new SubscribePayload();
		payload.billingKey = billingKey;
		payload.orderName = "예약된 정기결제";
		payload.price = price;
		payload.orderId = "sub_" + System.currentTimeMillis();

		long now = System.currentTimeMillis();
		long futureTime = now + (10 * 1000); // 10초 뒤 실행
		payload.reserveExecuteAt = String.valueOf(futureTime);

		try {
			return bootpay.reserveSubscribe(payload);
		} catch (Exception e) {
			throw new RuntimeException("정기 결제 예약 중 오류 발생: " + e.getMessage());
		}
	}

	/**
	 * ✅ 결제 취소
	 */
	public HashMap<String, Object> cancelPayment(String receiptId, String reason) {
		// ✅ Bootpay에 Access Token 설정
		String accessToken = getAccessToken();
		bootpay.setToken(accessToken);

		Cancel cancel = new Cancel();
		cancel.receiptId = receiptId; // 취소할 결제 건의 영수증 ID
		cancel.cancelUsername = "관리자"; // 취소 요청자 (필요하면 변경 가능)
		cancel.cancelMessage = reason; // 취소 사유

		try {
			return bootpay.receiptCancel(cancel); // Bootpay API 호출
		} catch (Exception e) {
			throw new RuntimeException("결제 취소 중 오류 발생: " + e.getMessage());
		}
	}

	/**
	 * ✅ 정기 결제 취소 (Billing Key 삭제)
	 */
	public HashMap<String, Object> deleteBillingKey(String billingKey) {
		// ✅ Bootpay에 Access Token 설정
		String accessToken = getAccessToken();
		bootpay.setToken(accessToken);

		try {
			return bootpay.destroyBillingKey(billingKey);
		} catch (Exception e) {
			throw new RuntimeException("Billing Key 삭제 중 오류 발생: " + e.getMessage());
		}
	}
}
