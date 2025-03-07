package com.achiko.backend.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.achiko.backend.dto.FavoriteDTO;
import com.achiko.backend.dto.LoginUserDetails;
import com.achiko.backend.dto.ReviewDTO;
import com.achiko.backend.dto.UserDTO;
import com.achiko.backend.dto.ViewingDTO;
import com.achiko.backend.service.MypageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MypageViewController {

	private final MypageService mypageService;

	// 마이페이지 화면 요청
	@GetMapping("/mypage/mypageView")
	public String mypageView(
			@AuthenticationPrincipal LoginUserDetails loginUser
			, Model model
			) {
		if (loginUser == null) {
			return "redirect:/user/login";
		}
		model.addAttribute("loginUser", loginUser);
				
		String loginId = loginUser.getLoginId();
		model.addAttribute("loginId", loginId);
		Long userId = loginUser.getUserId();
		
		UserDTO userDTO = mypageService.getMypage(loginId);
		model.addAttribute("userDTO", userDTO);
		
//		List<ViewingDTO> viewingList = mypageService.getViewingList(userId);
//        model.addAttribute("viewingList", viewingList);
        
        List<FavoriteDTO> favoriteList = mypageService.getFavoriteList(userId);
        model.addAttribute("favoriteList", favoriteList);
        
//        List<ReviewDTO> receivedReviewList = mypageService.getReceivedReviewList(userId);
//        model.addAttribute("receivedReviewList", receivedReviewList);
//        
//        List<ReviewDTO> writtenReviewList = mypageService.getWrittenReviewList(userId);
//        model.addAttribute("writtenReviewList", writtenReviewList);
			        
		return "mypage/mypageView";
	}
	
	// 마이페이지 수정화면 요청
	/*
	 * @GetMapping("/mypage/profileRegist") public String
	 * profileRegist(@AuthenticationPrincipal LoginUserDetails loginUser, Model
	 * model) { UserDTO userDTO = mypageService.getMypage(loginUser.getLoginId());
	 * model.addAttribute("userDTO", userDTO); model.addAttribute("loginUser",
	 * loginUser); return "mypage/profileRegist"; }
	 */
	
}