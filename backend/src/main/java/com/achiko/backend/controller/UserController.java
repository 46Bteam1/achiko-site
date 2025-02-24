package com.achiko.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.achiko.backend.dto.UserDTO;
import com.achiko.backend.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	@GetMapping("/regist")	// 회원가입 페이지
	public String regist() {
		return "/user/regist";
	}
	
	@PostMapping("/regist")		// 회원가입
	public String regist(@ModelAttribute UserDTO userDTO) {
		
		userService.regist(userDTO);
		return "redirect:/";
	}
	
	@GetMapping("/login")		// 로그인페이지
	public String login() {
		return "/user/login";
	}
	
	@GetMapping("/findLoginId")
	public String findLoginId() {
		return "/user/findLoginId";
	}
	
	@PostMapping("/findLoginId")
	public String findLoginId(@ModelAttribute UserDTO userDTO, Model model) {
		String findedId = userService.findLoginId(userDTO);
		model.addAttribute("findedId", findedId);
		return "/user/findLoginIdResult";
	}
	
	@GetMapping("/findPassword")
	public String findPassword() {
		return "/user/findPassword";
	}
	
	@PostMapping("/findPassword")
	public String findPassword(@ModelAttribute UserDTO userDTO, Model model) {
		String tempPw = userService.findPassword(userDTO);
		model.addAttribute("tempPw", tempPw);
		return "/user/findPasswordResult";
	}
}
