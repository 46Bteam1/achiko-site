package com.achiko.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaymentResponseDto {
    private String qrCodeUrl;      // PayPay 결제 QR 코드 URL
    private String merchantPaymentId; // PayPay에서 생성된 주문 ID
}
