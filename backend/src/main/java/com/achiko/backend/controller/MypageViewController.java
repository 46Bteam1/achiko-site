package com.achiko.backend.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.achiko.backend.dto.FavoriteDTO;
import com.achiko.backend.dto.LoginUserDetails;
import com.achiko.backend.dto.PrincipalDetails;
import com.achiko.backend.dto.ReviewDTO;
import com.achiko.backend.dto.ReviewReplyDTO;
import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.dto.UserDTO;
import com.achiko.backend.dto.ViewingDTO;
import com.achiko.backend.service.MypageService;
import com.achiko.backend.service.ShareService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MypageViewController {

	private final MypageService mypageService;
	private final ShareService shareService;

	// 마이페이지 화면 요청
	@GetMapping("/mypage/mypageView")
	public String mypageView(@AuthenticationPrincipal PrincipalDetails loginUser, Model model) {
		if (loginUser == null) {
			return "redirect:/user/login";
		}
		model.addAttribute("loginUser", loginUser);

		String loginId = loginUser.getLoginId();
		model.addAttribute("loginId", loginId);
		Long userId = loginUser.getUserId();

		UserDTO userDTO = mypageService.getMypage(userId);
		model.addAttribute("userDTO", userDTO);

		return "mypage/mypageView";
	}

	@GetMapping("/mypage/mypageSample")
	public String showMypageSample(@AuthenticationPrincipal PrincipalDetails loginUser, Model model) {
		if (loginUser == null) {
			return "redirect:/user/login";
		}
		model.addAttribute("loginUser", loginUser);

		Long userId = loginUser.getUserId();
		UserDTO userDTO = mypageService.getMypage(userId);
		model.addAttribute("userId", userId);
		model.addAttribute("userDTO", userDTO);

		List<ViewingDTO> viewingList = mypageService.getViewingList(userId);
		model.addAttribute("viewingList", viewingList);

		List<FavoriteDTO> favoriteList = mypageService.getFavoriteList(userId);
		model.addAttribute("favoriteList", favoriteList);

		List<ReviewDTO> receivedReviewList = mypageService.getReceivedReviewList(userId);
		model.addAttribute("receivedReviewList", receivedReviewList);

		List<ReviewDTO> writtenReviewList = mypageService.getWrittenReviewList(userId);
		model.addAttribute("writtenReviewList", writtenReviewList);

		List<ReviewReplyDTO> reviewReplyList = mypageService.getReviewReplyList(userId);
		model.addAttribute("reviewReplyList", reviewReplyList);

		List<ShareDTO> myShareList = mypageService.getMyShare(userId);
		model.addAttribute("myShareList", myShareList);

		return "mypage/mypageSample";
	}

}