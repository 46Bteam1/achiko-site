package com.achiko.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReviewWriteController {
	
	@GetMapping("/reviewWrite")
	public String reviewWrite() {
		return "review/reviewWrite";
	}
}
