package com.achiko.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SplineViewController {

	@GetMapping("/viewSpline")
	public String spline() {
		return "spline/splinePage";
	}

}
