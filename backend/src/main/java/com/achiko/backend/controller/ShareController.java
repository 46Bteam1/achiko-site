package com.achiko.backend.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.achiko.backend.dto.FavoriteDTO;
import com.achiko.backend.dto.RoommateDTO;
import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.dto.ShareFilesDTO;
import com.achiko.backend.dto.UserDTO;
import com.achiko.backend.entity.RegionEntity;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.RegionRepository;
import com.achiko.backend.repository.UserRepository;
import com.achiko.backend.service.FavoriteService;
import com.achiko.backend.service.ShareFilesService;
import com.achiko.backend.service.ShareService;
import com.achiko.backend.service.UserService;
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
    private final FavoriteService favoriteService;
    private final UserService userService;
    
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
    /**
     * 글 상세 조회 페이지 URL 예: /share/selectOne?shareId=1
     */
    @GetMapping("/share/selectOne")
    public String selectOne(@RequestParam("shareId") Long shareId, Model model, Principal principal) {
        // ShareService를 통해 shareId에 해당하는 게시글 정보를 조회
        ShareDTO shareDTO = shareService.getShareById(shareId);
        if (shareDTO == null) {
            return "redirect:/";
        }

        // 이미지 목록 조회
        List<ShareFilesDTO> fileList = shareFilesService.getFilesByShareId(shareId);

        // 첫 번째 이미지의 절대 URL 설정 (없으면 기본 이미지)
        String firstImageUrl = (fileList != null && !fileList.isEmpty()) 
                ? "http://localhost:9905" + fileList.get(0).getFileUrl()
                : "http://localhost:9905/images/default.webp";
        // String firstImageUrl = (fileList != null && !fileList.isEmpty()) 
        //         ? "https://achiko.site" + fileList.get(0).getFileUrl()
        //         : "https://achiko.site/images/default.webp";

        // 파일 목록을 ShareDTO에 설정 (추후 view에서 사용)
        shareDTO.setFileList(fileList);

        // favorite count 조회 (해당 게시글에 찜하기가 몇 개 등록되었는지 확인)
        long count = favoriteService.countFavorites(shareId);
        shareDTO.setFavoriteCount(count);

        // 로그인한 경우, 현재 사용자가 해당 게시글을 찜했는지 확인하여 isFavorite 설정
        if (principal != null) {
            UserEntity loggedUser = userRepository.findByLoginId(principal.getName());
            if (loggedUser != null) {
                boolean isFav = favoriteService.isFavorite(shareId, loggedUser.getUserId());
                shareDTO.setIsFavorite(isFav);
            }
        }
        
        // 확정된 룸메이트 찾기
        List<RoommateDTO> roommateList = shareService.findRoommate(shareId);
        System.out.println(roommateList+"여긴강");
        model.addAttribute("roommateList", roommateList);

        // 모델에 공유글, 파일 목록, 첫 이미지 URL, API 키 등을 추가하여 뷰에 전달
        model.addAttribute("share", shareDTO);
        model.addAttribute("fileList", fileList);
        model.addAttribute("firstImageUrl", firstImageUrl);
        model.addAttribute("googleApiKey", googleApiKey);
        model.addAttribute("kakaoJavaScriptKey", kakaoJavaScriptKey);

        // 작성자(Host) 정보를 별도로 조회
        UserDTO hostUser = userService.selectOneUser(shareDTO.getHostId());
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
     */
    @PostMapping("/share/write")
    public String writeSubmit(@ModelAttribute ShareDTO shareDTO,
                              @RequestParam(value = "sessionId", required = false) String sessionId,
                              Principal principal,
                              Model model) {
        UserEntity user = userRepository.findByLoginId(principal.getName());
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        shareDTO.setHostId(user.getUserId());
  
        ShareDTO saved = shareService.saveShare(shareDTO);
  
        if (sessionId != null && !sessionId.isEmpty()) {
            shareFilesService.bindFilesToShare(sessionId, saved.getShareId());
        }
  
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
        UserEntity user = userRepository.findByLoginId(principal.getName());
        if (user == null || !user.getUserId().equals(shareDTO.getHostId())) {
            return "redirect:/share/selectOne?shareId=" + shareId;
        }
  
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
     */
    @PostMapping("/share/update")
    public String updateShare(@ModelAttribute ShareDTO shareDTO, Principal principal, Model model) {
        UserEntity user = userRepository.findByLoginId(principal.getName());
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + principal.getName());
        }
        shareDTO.setHostId(user.getUserId());
        shareDTO.setCreatedAt(LocalDateTime.now());
  
        ShareDTO updated = shareService.updateShare(shareDTO);
        
        
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
     * ★ [찜하기 추가 API] - ShareController 내에서 Favorite 기능 처리
     */
    @PostMapping("/favorite/set")
    @ResponseBody
    public ResponseEntity<FavoriteDTO> setFavorite(@RequestBody Map<String, Object> payload, Principal principal) {
        Object shareIdObj = payload.get("shareId");
        if (shareIdObj == null) {
            return ResponseEntity.badRequest().build();
        }
        Long shareId;
        try {
            shareId = Long.valueOf(shareIdObj.toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
        UserEntity user = userRepository.findByLoginId(principal.getName());
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        FavoriteDTO favoriteDTO = favoriteService.addFavorite(shareId, user.getUserId());
        return ResponseEntity.ok(favoriteDTO);
    }
  
    /**
     * ★ [찜 취소 API] - ShareController 내에서 Favorite 기능 처리
     */
    @DeleteMapping("/favorite/cancel")
    @ResponseBody
    public ResponseEntity<Void> cancelFavorite(@RequestParam("shareId") Long shareId, Principal principal) {
        if (shareId == null) {
            return ResponseEntity.badRequest().build();
        }
        UserEntity user = userRepository.findByLoginId(principal.getName());
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        favoriteService.cancelFavorite(shareId, user.getUserId());
        return ResponseEntity.ok().build();
    }
    
    /**
     * [GET] /api/geocode - Google Geocoding API 호출
     */
    @GetMapping("/api/geocode")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getGeocode(@RequestParam("lat") double lat,
                                                         @RequestParam("lng") double lng) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
                     + lat + "," + lng + "&language=en&key=" + googleApiKey;
        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.getForObject(url, String.class);
  
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
