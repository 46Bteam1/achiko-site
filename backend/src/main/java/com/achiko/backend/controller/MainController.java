package com.achiko.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.service.ShareService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {
	@Value("${google.api-key}")
	private String googleApiKey;

    private final ShareService shareService; 

    @GetMapping({"/", ""})
    public String index(Model model) {
        List<ShareDTO> shareList = shareService.getShareListAll(); 
        model.addAttribute("shareList", shareList); 
        model.addAttribute("googleApiKey", googleApiKey);
        return "index"; 
    }
}
