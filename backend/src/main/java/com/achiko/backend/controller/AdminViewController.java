package com.achiko.backend.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.service.AdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminViewController {

	private final AdminService adminService;

	// 마이페이지 화면 요청
	@GetMapping("/admin/adminPage")
	public String adminPage(Model model) {
		List<ShareDTO> shareList = adminService.getShareList();
		if (shareList.size() == 0) {
			return null;
		}
		model.addAttribute("shareList", shareList);
		log.info("====== {}", shareList);

		return "admin/adminPage";
	}

}