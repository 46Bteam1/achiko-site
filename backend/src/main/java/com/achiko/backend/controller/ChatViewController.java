package com.achiko.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatViewController {

	@GetMapping("/chatList")
	public String chatList() {
		return "chat/chatList";
	}
	
	@GetMapping("/chatRoom")
	public String chatRoom() {
		return "chat/chatRoom";
	}
}
