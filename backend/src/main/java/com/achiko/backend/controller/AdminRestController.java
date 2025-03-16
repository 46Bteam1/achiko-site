package com.achiko.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.service.AdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminRestController {

	private final AdminService adminService;

	// 게시글 삭제
	@DeleteMapping("/deletePost/{shareId}")
	public ResponseEntity<String> deletePost(@PathVariable Long shareId) {
		boolean isDeleted = adminService.deleteShare(shareId);
		if (isDeleted) {
			return ResponseEntity.ok("삭제 완료");
		} else {
			return ResponseEntity.badRequest().body("삭제 실패: 존재하지 않는 게시글");
		}
	}

	@GetMapping("/malicious-users")
    public List<UserEntity> getMaliciousUsers() {
        return adminService.getMaliciousUsers();
    }

	@PostMapping("/update-malicious")
    @ResponseBody
    public String updateMaliciousUsers() {
        adminService.updateMaliciousUsers();
        return "success";
    }
	
	// 채팅방 전체 조회

}