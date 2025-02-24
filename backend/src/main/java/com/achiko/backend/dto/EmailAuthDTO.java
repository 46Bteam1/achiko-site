package com.achiko.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class EmailAuthDTO {
	private String email; // 이메일 주소
    private String authCode; // 인증번호
    private LocalDateTime expiredAt; // 인증번호 만료 시간
}
