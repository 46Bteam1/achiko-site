package com.achiko.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {
    private int amount;        // 결제 금액
    private String redirectUrl; // 결제 완료 후 이동할 URL
}
