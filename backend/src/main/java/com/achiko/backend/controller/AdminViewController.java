package com.achiko.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminViewController {

	// 마이페이지 화면 요청
	@GetMapping("/admin/adminPage")
	public String adminPage() {

		return "admin/adminPage";
	}

}