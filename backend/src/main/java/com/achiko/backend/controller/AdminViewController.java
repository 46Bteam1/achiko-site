//package com.achiko.backend.controller;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//@RequestMapping("/admin")
//public class AdminViewController {
//
//	private final AdminService adminService;
//	
//	@GetMapping("/adminpage")
//	public String adminpage() {
//		return "admin/adminPage";
//	}
//	
//    // 회원 모니터링 페이지
//    @GetMapping("/user-monitoring")
//    public String userMonitoring(Model model) {
//        model.addAttribute("users", adminService.getAllUsers());
//        return "admin/user-monitoring";
//    }
//	
//    // 게시글 관리 페이지
//    @GetMapping("/post-management")
//    public String postManagement(Model model) {
//        model.addAttribute("posts", adminService.getAllPosts());
//        return "admin/post-management";
//    }
//}
