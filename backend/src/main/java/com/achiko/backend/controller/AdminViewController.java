package com.achiko.backend.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.achiko.backend.dto.ReviewDTO;
import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.dto.UserDTO;
import com.achiko.backend.dto.UserReportDTO;
import com.achiko.backend.dto.ViewingDTO;
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
		
		List<ViewingDTO> viewingList = adminService.getViewingList();
		if (viewingList.size() == 0) {
			return null;
		}
		model.addAttribute("viewingList", viewingList);
		
		List<ReviewDTO> reviewList = adminService.getAllReviews();
		if (reviewList.size() == 0) {
			return null;
		}
		model.addAttribute("reviewList", reviewList);
		
		List<UserReportDTO> userReportList = adminService.getAllUserReport();
		if (userReportList.size() == 0) {
			return null;
		}
		model.addAttribute("userReportList", userReportList);
		
//		List<UserDTO> reportedUserList = adminService.getIsMaliciousTrue();
//		if (reportedUserList.size() == 0) {
//			return null;
//		}
//		model.addAttribute("reportedUserList", reportedUserList);
		
		return "admin/adminPage";
	}

}