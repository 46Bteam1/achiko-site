package com.achiko.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.achiko.backend.dto.UserDTO;
import com.achiko.backend.service.MypageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
@RestController
@Tag(name = "MyPage", description = "마이페이지 관련 API")
@RequestMapping("/api/mypage") // or /user?
@RequiredArgsConstructor
public class MypageRestController {

	private final MypageService mypageService;
//	private final FavoriteService favoriteService;
//	private final ViewingService viewingService;
	// TODO 나중에 살릴 것


	// 사용자 단일 데이터 조회 (JSON)
	@Operation(summary = "사용자 정보 조회", description = "특정 사용자의 데이터를 반환합니다.")
	@GetMapping("/{userId}")
	public ResponseEntity<UserDTO> getUserInfo(@RequestParam("userId") Long userId	// 쿼리(주소창)에 아이디를 보내주겠다<임시>
			//@AuthenticationPrincipal UserDetails user
			) {
		UserDTO userDTO = mypageService.getUserByUserId(userId);
		return ResponseEntity.ok(userDTO);
	}

	// 사용자 정보 일부 업데이트 (PATCH)
    @Operation(summary = "데이터 부분 업데이트 (PATCH)", description = "기존 데이터의 특정 필드만 수정합니다.")
    @PatchMapping("/{userId}")
    public ResponseEntity<UserDTO> patchMypage(@PathVariable("userId") Long userId,
                               @RequestBody UserDTO userDTO) {
    	UserDTO updatedUserInfo = mypageService.patchMypage(userId, userDTO);
        return ResponseEntity.ok(updatedUserInfo);
    }
 
}
