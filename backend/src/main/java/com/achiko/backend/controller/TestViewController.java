package com.achiko.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "View", description = "View API")
@Controller
@RequestMapping("/testTemp")
public class TestViewController {

    @Operation(summary = "테스트페이지 조회", description = "testPage를 반환합니다.")
    @GetMapping("/testPage")
    public String testPage(Model model) {
        model.addAttribute("message", "REST API 기반 테스트 페이지입니다!");
        return "testTemp/testPage";  // Thymeleaf 파일명 (확장자 제외)
    }
}
