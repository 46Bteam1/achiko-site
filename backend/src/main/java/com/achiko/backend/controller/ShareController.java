package com.achiko.backend.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.UserRepository;
import com.achiko.backend.service.ShareService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ShareController {

	@Value("${google.api-key}")
	private String googleApiKey;

	private final ShareService shareService;
	private final UserRepository userRepository;
	
	@ResponseBody
	@GetMapping("/share/selectAll")
	public List<ShareDTO> selectAll() {
		List<ShareDTO> shareList = shareService.getShareListAll();
		if(shareList.size() == 0) {
			return null;
		}
		return shareList;
	}

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
	
	@GetMapping("/api/geocode")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getGeocode(@RequestParam("lat") double lat,
	                                                       @RequestParam("lng") double lng) {
	    // ★ Google Geocoding API 호출 URL 구성 (영문 주소 반환)
	    String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" 
	                 + lat + "," + lng + "&language=en&key=" + googleApiKey;
	    RestTemplate restTemplate = new RestTemplate();
	    String jsonResponse = restTemplate.getForObject(url, String.class);
	    
	    // ★ JSON 문자열을 Map으로 변환 (Jackson 라이브러리 사용)
	    ObjectMapper mapper = new ObjectMapper();
	    Map<String, Object> responseMap;
	    try {
	        responseMap = mapper.readValue(jsonResponse, new TypeReference<Map<String, Object>>() {});
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	    return ResponseEntity.ok(responseMap);
	}
	
}
