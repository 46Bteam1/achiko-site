package com.achiko.backend.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.UserRepository;
import com.achiko.backend.service.ShareService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ShareController {

	@Value("${google.api-key}")
	private String googleApiKey;

	private final ShareService shareService;
	private final UserRepository userRepository;

	/**
	 * 글 상세 조회 페이지 URL 예: /share/selectOne?shareId=1
	 */
	@GetMapping("/share/selectOne")
	public String selectOne(@RequestParam("shareId") Long shareId, Model model) {
		ShareDTO shareDTO = shareService.getShareById(shareId);
		if (shareDTO == null) {
			return "redirect:/"; // 또는 에러 페이지
		}
		model.addAttribute("share", shareDTO);
		model.addAttribute("googleApiKey", googleApiKey);
		// 기타 모델 데이터 추가
		return "share/selectOne";
	}

	/**
	 * [GET] 글 작성 페이지 URL: /share/write
	 */
	@GetMapping("/share/write")
	public String writeForm(Model model) {
		model.addAttribute("googleApiKey", googleApiKey);
		return "share/write";
	}

	/**
	 * [POST] 게시물 등록 처리 URL: /share/write
	 */
	@PostMapping("/share/write")
	public String writeSubmit(@ModelAttribute ShareDTO shareDTO, Principal principal, Model model) {
		// ★ principal.getName()는 로그인 아이디이므로, 이를 통해 UserEntity를 조회합니다.
		UserEntity user = userRepository.findByLoginId(principal.getName());
		if (user == null) {
			throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + principal.getName());
		}
		shareDTO.setHostId(user.getUserId());

		ShareDTO saved = shareService.saveShare(shareDTO);
		return "redirect:/share/selectOne?shareId=" + saved.getShareId();
	}
}
