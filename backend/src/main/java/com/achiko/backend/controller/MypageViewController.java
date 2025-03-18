package com.achiko.backend.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.achiko.backend.dto.FavoriteDTO;
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

	// 게스트/호스트 전환 요청을 처리하는 메서드
	@PostMapping("/mypage/changeToGuest")
	public String changeToGuest(@RequestParam("userId") Long userId, RedirectAttributes redirectAttributes) {

		String result = mypageService.changeToGuest(userId);

		if ("MATCHING_IN_PROGRESS".equals(result)) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"매칭이 진행 중인 쉐어하우스가 있습니다. 매칭이 완료 되었다면 진행 중인 쉐어를 종료해주세요.");
			return "redirect:/mypage/mypageSample";
		}
		return "redirect:/mypage/mypageView";
	}

	// 매칭이 완료 된 쉐어 글의 상태(status)를 종료(closed)
	@PostMapping("/mypage/closeShare")
	public String closeShare(@RequestParam("userId") Long userId, @AuthenticationPrincipal PrincipalDetails loginUser, RedirectAttributes redirectAttributes) {
		boolean isClosed = mypageService.closeShare(userId, loginUser);

		if (isClosed) {
			redirectAttributes.addFlashAttribute("successMessage", "쉐어하우스 매칭이 종료되었습니다.");
		} else {
			redirectAttributes.addFlashAttribute("errorMessage", "종료할 쉐어하우스가 없습니다.");
		}

		return "redirect:/mypage/mypageView";
	}
}