package com.achiko.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.achiko.backend.dto.CustomOAuth2User;
import com.achiko.backend.dto.LoginUserDetails;
import com.achiko.backend.dto.UserDTO;
import com.achiko.backend.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	private final HttpSession httpSession;
	
	@GetMapping("/regist")	// 회원가입 페이지
	public String regist() {
		return "user/regist";
	}
	
	@PostMapping("/regist")		// 회원가입
	public String regist(@ModelAttribute UserDTO userDTO) {
		
		userService.regist(userDTO);
		return "redirect:/";
	}
	
	@GetMapping("/login")		// 로그인페이지
	public String login(@RequestParam(value = "error", required = false) String error,
						@RequestParam(value = "exception", required = false) String exception,
						Model model) {
		model.addAttribute("error", error);
		model.addAttribute("exception", exception);
		return "user/login";
	}
	
	@GetMapping("/findLoginId")	//아이디 찾기
	public String findLoginId() {
		return "user/findLoginId";
	}
	
	@PostMapping("/findLoginId")
	public String findLoginId(@ModelAttribute UserDTO userDTO, Model model) {
		String findedId = userService.findLoginId(userDTO);
		model.addAttribute("findedId", findedId);
		return "user/findLoginIdResult";
	}
	
	@GetMapping("/findPassword")
	public String findPassword() {
		return "user/findPassword";
	}
	
	@PostMapping("/findPassword")
	public String findPassword(@ModelAttribute UserDTO userDTO, Model model) {
		String tempPw = userService.findPassword(userDTO);
		model.addAttribute("tempPw", tempPw);
		return "user/findPasswordResult";
	}
	
	@PostMapping("/chkIdDuplication")
	@ResponseBody
	public boolean chkIdDuplication(@RequestParam(name="loginId") String loginId) {
		boolean isDup = userService.isIdAvailable(loginId);
		return isDup;
	}
	
	@PostMapping("/chkEmailDuplication")
	@ResponseBody
	public boolean chkEmailDuplication(@RequestParam(name="email") String email) {
		boolean isDup = userService.isEmailAvailable(email);
		return isDup;
	}
	
	@GetMapping("/temp")
	public String temp() {
		return "user/temp";
	}
	
	@DeleteMapping("/deleteUser")
	@ResponseBody
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal LoginUserDetails userDetails) {
        if (userDetails != null) {
        	String loginId = userDetails.getLoginId(); // 현재 로그인된 사용자 ID 가져오기
            userService.deleteUser(loginId); // 사용자 삭제 서비스 호출

            httpSession.invalidate(); // 세션 무효화 (로그아웃 처리)
            return ResponseEntity.ok("회원탈퇴가 완료되었습니다.");
        }else {
        	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();	
        	
        	CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            
            String loginId = customOAuth2User.getUsername();
            userService.deleteUser(loginId);
            httpSession.invalidate(); // 세션 무효화 (로그아웃 처리)
            return ResponseEntity.ok("회원탈퇴가 완료되었습니다.");
        }
    }
}
