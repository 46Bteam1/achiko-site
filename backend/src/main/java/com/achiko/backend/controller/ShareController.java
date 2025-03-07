package com.achiko.backend.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
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
    public String selectOne(@RequestParam("shareId") Long shareId, Model model, Principal principal) {
        ShareDTO shareDTO = shareService.getShareById(shareId);
        if (shareDTO == null) {
            return "redirect:/";
        }

        // 이미지 목록 조회
        List<ShareFilesDTO> fileList = shareFilesService.getFilesByShareId(shareId);

        // 첫 번째 이미지의 절대 URL 설정 (없으면 기본 이미지)
        String firstImageUrl = (fileList != null && !fileList.isEmpty()) 
            ? "http://localhost:8080" + fileList.get(0).getFileUrl()
            : "http://localhost:8080/images/default.webp";

        model.addAttribute("share", shareDTO);
        model.addAttribute("fileList", fileList);
        model.addAttribute("firstImageUrl", firstImageUrl);  // ✅ 카카오 공유용 이미지 URL 추가
        model.addAttribute("googleApiKey", googleApiKey);
        model.addAttribute("kakaoJavaScriptKey", kakaoJavaScriptKey);

        // 작성자(Host) 정보를 별도로 조회
        UserEntity hostUser = userRepository.findById(shareDTO.getHostId()).orElse(null);
        model.addAttribute("hostUser", hostUser);

        // 현재 로그인한 사용자 정보 확인 및 소유자 여부 체크
        boolean isOwner = false;
        if (principal != null) {
            UserEntity loggedUser = userRepository.findByLoginId(principal.getName());
            if (loggedUser != null && loggedUser.getUserId().equals(shareDTO.getHostId())) {
                isOwner = true;
            }
            model.addAttribute("loggedUser", loggedUser);
        }
        model.addAttribute("isOwner", isOwner);

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
    public String updateForm(@RequestParam("shareId") Long shareId, Model model, Principal principal) {
        ShareDTO shareDTO = shareService.getShareById(shareId);
        if (shareDTO == null) {
            return "redirect:/";
        }
        // 소유자 여부 확인
        UserEntity user = userRepository.findByLoginId(principal.getName());
        if (user == null || !user.getUserId().equals(shareDTO.getHostId())) {
            // 소유자가 아니라면 상세 페이지로 리다이렉트하거나 에러 처리
            return "redirect:/share/selectOne?shareId=" + shareId;
        }

        // 기타 수정 폼에 필요한 데이터 전달
        RegionEntity region = regionRepository.findById(shareDTO.getRegionId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("해당 region을 찾을 수 없습니다."));
        int provinceId = region.getProvince().getProvinceId();

        List<ShareFilesDTO> fileList = shareFilesService.getFilesByShareId(shareId);
        model.addAttribute("share", shareDTO);
        model.addAttribute("provinceId", provinceId);
        model.addAttribute("fileList", fileList);
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
    public String deleteShare(@RequestParam("shareId") Long shareId, Principal principal, RedirectAttributes rttr) {
        ShareDTO shareDTO = shareService.getShareById(shareId);
        if (shareDTO == null) {
            rttr.addFlashAttribute("error", "게시글이 존재하지 않습니다.");
            return "redirect:/";
        }
        UserEntity user = userRepository.findByLoginId(principal.getName());
        if (user == null || !user.getUserId().equals(shareDTO.getHostId())) {
            rttr.addFlashAttribute("error", "게시물을 삭제할 권한이 없습니다.");
            return "redirect:/share/selectOne?shareId=" + shareId;
        }
        shareService.deleteShare(shareId);
        rttr.addFlashAttribute("message", "게시물이 삭제되었습니다.");
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
    
    @RestController
    @RequestMapping("/api/config")
    public class ConfigController {

        @Value("${kakao.javascript.key}") 
        private String kakaoJavaScriptKey;

        @GetMapping("/kakao-key")
        public ResponseEntity<Map<String, String>> getKakaoKey() {
            Map<String, String> response = new HashMap<>();
            response.put("kakaoKey", kakaoJavaScriptKey);
            return ResponseEntity.ok(response);
        }
    }

}
