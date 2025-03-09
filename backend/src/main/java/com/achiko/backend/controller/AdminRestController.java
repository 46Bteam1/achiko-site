package com.achiko.backend.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.service.AdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminRestController {

	private final AdminService adminService;

    // 게시글 목록 조회
    @GetMapping("/post-monitoring")
    public List<ShareDTO> getAllShares(Model model) {
    	List<ShareDTO> shareList = adminService.getShareList();
		if(shareList.size() == 0) {
			return null;
		}
		model.addAttribute("shareList", shareList);
		log.info("====== {}", shareList);
		return shareList;
    }
    
    
    
    
}