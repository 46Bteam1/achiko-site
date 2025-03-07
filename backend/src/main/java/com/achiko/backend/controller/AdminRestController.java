package com.achiko.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.service.AdminService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminRestController {

	private final AdminService adminService;

    // 게시글 목록 조회
    @GetMapping("/shares")
    public ResponseEntity<List<ShareDTO>> getShares() {
        return ResponseEntity.ok(adminService.getAllShares());
    }
}