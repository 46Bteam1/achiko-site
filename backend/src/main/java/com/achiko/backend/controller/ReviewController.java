package com.achiko.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
@Slf4j

public class ReviewController {
	
	
	
	@GetMapping("/reviewWrite")
	public String reviewWrite() {
		return "/board/reviewWrite";
	}
	

	
	
}
