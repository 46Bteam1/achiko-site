package com.achiko.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReviewViewController {
	
	@GetMapping("/reviewView")
	public String reviewView() {
		return "review/reviewPage";
	}
}
