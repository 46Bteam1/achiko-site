package com.achiko.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.achiko.backend.dto.PrincipalDetails;
import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.dto.ShareFilesDTO;
import com.achiko.backend.service.ChatService;
import com.achiko.backend.service.ShareFilesService;
import com.achiko.backend.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatViewController {
	private final UserService userService;
	private final ChatService chatService;
	private final ShareFilesService shareFilesService;
	
    @Value("${google.api-key}")
    private String googleApiKey;

	@GetMapping("/chatList")
	public String chatList(@RequestParam(name="chatroomId") Long chatroomId, Model model,
			@AuthenticationPrincipal PrincipalDetails loginUser) {
		Long userId = userService.getUserId(loginUser.getLoginId());
		String nickname = userService.getUserName(loginUser.getUserId());
		String role = userService.getIsGuest(userId);
		Long shareId = chatService.getShareId(chatroomId);
		ShareDTO shareInfo = chatService.shareInfoByRoomId(chatroomId);
		
        List<ShareFilesDTO> fileList = shareFilesService.getFilesByShareId(shareId);
		
		model.addAttribute("chatroomId", chatroomId);
		model.addAttribute("userId", userId);
		model.addAttribute("nickname", nickname);
		model.addAttribute("role", role);
		model.addAttribute("shareId", shareId);
		
		model.addAttribute("share", shareInfo);
		model.addAttribute("fileList", fileList);
		model.addAttribute("googleApiKey", googleApiKey);
		
		return "chat/chatList";
	}
	
	@GetMapping("/chatRooms")
	public String chatRoom(Model model,
			@AuthenticationPrincipal PrincipalDetails loginUser) {
		model.addAttribute("user", loginUser);
		return "chat/chatRooms";
	}
}
