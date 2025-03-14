package com.achiko.backend.service;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.UserRepository;

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.model.request.Cancel;

@Service
public class SubscribeService {

	private final UserRepository userRepository;
	private final Bootpay bootpay;

	@Value("${bootpay.api.cancel-url}")
	private String bootpayCancelUrl;

	@Value("${bootpay.application-id}")
	private String applicationId;

	@Value("${bootpay.private-key}")
	private String privateKey;

	public SubscribeService(@Value("${bootpay.application-id}") String applicationId,
			@Value("${bootpay.private-key}") String privateKey, UserRepository userRepository) {
		this.userRepository = userRepository;
		this.bootpay = new Bootpay(applicationId, privateKey);
	}

	/**
	 * BootPay Access Token 발급
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
	 * Bootpay 결제 취소 요청
	 */
	public HashMap<String, Object> cancelSubscription(Long userId) {
		Optional<UserEntity> optionalUser = userRepository.findById(userId);

		if (optionalUser.isEmpty() || optionalUser.get().getReceiptId() == null) {
			throw new RuntimeException("해당 유저의 결제 정보를 찾을 수 없습니다.");
		}

		UserEntity user = optionalUser.get();
		String receiptId = user.getReceiptId(); // receiptId가 isSubscribed에 저장됨

		// Bootpay Access Token 가져오기
		String accessToken = getAccessToken();
		bootpay.setToken(accessToken);

		Cancel cancel = new Cancel();
		cancel.receiptId = receiptId; // 취소할 결제 건의 영수증 ID
		cancel.cancelUsername = "관리자"; // 취소 요청자
		cancel.cancelMessage = "사용자 요청으로 결제 취소"; // 취소 사유

		try {
			// Bootpay API를 사용하여 결제 취소 요청
			HashMap<String, Object> response = bootpay.receiptCancel(cancel);
			if (response.get("error_code") == null) { // 성공
				// DB에서 해당 유저의 `isSubscribed` 값을 null로 변경
				user.setReceiptId(null);
				userRepository.save(user); // 변경사항 저장

				return response;
			} else {
				throw new RuntimeException("BootPay 결제 취소 실패: " + response);
			}
		} catch (Exception e) {
			throw new RuntimeException("결제 취소 중 오류 발생: " + e.getMessage());
		}
	}

	public String getUserSubscriptionStatus(Long userId) {
		return userRepository.findById(userId).map(UserEntity::getReceiptId).orElse(null); // isSubscribed 값 반환
	}

	/**
	 * 사용자 구독 상태 업데이트 (receiptId 변경)
	 */
	@Transactional
	public void updateSubscriptionStatus(Long userId, String receiptId) {
		UserEntity userEntity = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId + "번의 사용자"));

		userEntity.setReceiptId(receiptId);
		userRepository.save(userEntity);

	}
}
