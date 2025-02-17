package com.achiko.backend.controller;

import org.springframework.stereotype.Controller;
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
	
	@GetMapping("/regist")
	public String regist() {
		return "/user/regist";
	}
	
	@PostMapping("/regist")
	public String regist(@ModelAttribute UserDTO userDTO) {
		
		userService.regist(userDTO);
		return "redirect:/";
	}
	
	@GetMapping("/login")
	public String login() {
		return "/user/login";
	}
	
}
