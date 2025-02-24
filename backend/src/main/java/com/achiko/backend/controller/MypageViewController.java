package com.achiko.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.achiko.backend.service.MypageService;

import io.swagger.v3.oas.annotations.Operation;

@Controller
@RequestMapping("/mypage")
public class MypageViewController {
	
	private MypageService mypageService;
	
	
	// 마이페이지 요청
	@Operation(summary = "마이페이지 조회", description = "mypage를 반환합니다.")
	@GetMapping
    public String getMypage(Model model
    		//, @AuthenticationPrincipal UserDetails user
    		) {
       // model.addAttribute("user", user);
        return "mypage/mypage";
    }
    

}


/*
 *     // 마이페이지 수정 위한 화면 요청
	@GetMapping("/mypage/{userId}")
	public String patchMypage(@PathVariable Long userId) {
		mypageService.getUserByUserId(userId);
		return "mypage/patchMypage";
	}
 * 
 */