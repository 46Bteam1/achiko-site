package com.achiko.backend.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.dto.ShareFilesDTO;
import com.achiko.backend.entity.RegionEntity;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.RegionRepository;
import com.achiko.backend.repository.UserRepository;
import com.achiko.backend.service.ShareFilesService;
import com.achiko.backend.service.ShareService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ShareController {

    @Value("${google.api-key}")
    private String googleApiKey;
    
    @Value("${kakao.javascript.key}")
    private String kakaoJavaScriptKey;

	private final ShareService shareService;
	private final UserRepository userRepository;
	private final RegionRepository regionRepository;
	private final ShareFilesService shareFilesService;
	
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
            return "redirect:/";
        }

        // 1) 이미지 목록 조회
        List<ShareFilesDTO> fileList = shareFilesService.getFilesByShareId(shareId);

        model.addAttribute("share", shareDTO);
        model.addAttribute("fileList", fileList); 
        model.addAttribute("googleApiKey", googleApiKey);

        return "share/selectOne";
    }

    /**
     * [GET] 글 작성 페이지 URL: /share/write
     */
    @GetMapping("/share/write")
    public String writeForm(Model model) {
        model.addAttribute("googleApiKey", googleApiKey);

        String sessionId = UUID.randomUUID().toString();
        model.addAttribute("sessionId", sessionId);

        return "share/write";
    }

    /**
     * [POST] 게시물 등록 처리 URL: /share/write
     * 하나의 메서드로 통합하여 sessionId 파라미터를 optional로 받는다.
     */
    @PostMapping("/share/write")
    public String writeSubmit(@ModelAttribute ShareDTO shareDTO,
                              @RequestParam(value = "sessionId", required = false) String sessionId,
                              Principal principal,
                              Model model) {
        // 1) hostId 세팅
        UserEntity user = userRepository.findByLoginId(principal.getName());
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        shareDTO.setHostId(user.getUserId());

        // 2) Share 저장
        ShareDTO saved = shareService.saveShare(shareDTO);

        // 3) sessionId가 존재하면 bindFilesToShare 로직
        if (sessionId != null && !sessionId.isEmpty()) {
            shareFilesService.bindFilesToShare(sessionId, saved.getShareId());
        }

        // 4) 리다이렉트
        return "redirect:/share/selectOne?shareId=" + saved.getShareId();
    }

    /**
     * [GET] 수정 폼 페이지 URL: /share/update?shareId=1
     */
    @GetMapping("/share/update")
    public String updateForm(@RequestParam("shareId") Long shareId, Model model) {
        ShareDTO shareDTO = shareService.getShareById(shareId);
        if (shareDTO == null) {
            return "redirect:/"; // 또는 에러 페이지
        }
        // RegionEntity에서 provinceId 조회 (ProvinceEntity 내부의 provinceId 사용)
        RegionEntity region = regionRepository.findById(shareDTO.getRegionId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("해당 region을 찾을 수 없습니다."));
        int provinceId = region.getProvince().getProvinceId();

        // ★ 기존 파일 목록 조회: share_id에 해당하는 이미지들을 가져옴
        List<ShareFilesDTO> fileList = shareFilesService.getFilesByShareId(shareId);

        model.addAttribute("share", shareDTO);
        model.addAttribute("provinceId", provinceId);
        model.addAttribute("fileList", fileList); // update.html에 기존 사진 목록 전달
        model.addAttribute("googleApiKey", googleApiKey);
        return "share/update";
    }

    /**
     * ★ [파일 바인딩 API] 파일을 ShareEntity와 연결
     */
    @PostMapping("/share-files/bind")
    @ResponseBody
    public ResponseEntity<String> bindUploadedFiles(@RequestParam("shareId") Long shareId,
                                                    @RequestParam("sessionId") String sessionId) {
        try {
            shareFilesService.bindFilesToShare(sessionId, shareId);
            return ResponseEntity.ok("파일이 정상적으로 연결되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("파일 연결 실패: " + e.getMessage());
        }
    }



    /**
     * [POST] 수정 처리 URL: /share/update
     * 수정 시 작성일은 업데이트 시점의 현재 시간으로 임시 변경 (추후 updated_at 등으로 분리 가능)
     */
    @PostMapping("/share/update")
    public String updateShare(@ModelAttribute ShareDTO shareDTO, Principal principal, Model model) {
        // 로그인 사용자 정보 확인
        UserEntity user = userRepository.findByLoginId(principal.getName());
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + principal.getName());
        }
        shareDTO.setHostId(user.getUserId());
        shareDTO.setCreatedAt(LocalDateTime.now());

        // 업데이트 처리 (업데이트 로직은 ShareService에 구현)
        ShareDTO updated = shareService.updateShare(shareDTO);

        // 업데이트 후, 수정된 게시글 상세 페이지로 이동
        return "redirect:/share/selectOne?shareId=" + updated.getShareId();
    }

    /**
     * [GET] 게시물 삭제 처리 URL: /share/delete?shareId=...
     */
    @GetMapping("/share/delete")
    public String deleteShare(@RequestParam("shareId") Long shareId,
                              RedirectAttributes rttr) {
        // 삭제 처리 (삭제할 게시물이 존재하지 않으면 예외 발생)
        shareService.deleteShare(shareId);
        rttr.addFlashAttribute("message", "게시물이 삭제되었습니다.");
        // 삭제 후 이동할 페이지 - 필요에 따라 변경 (예: 공유글 목록 페이지)
        return "redirect:/";
    }

    /**
     * [GET] /api/geocode - Google Geocoding API 호출
     * lat, lng -> 영문 주소 반환
     */
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
