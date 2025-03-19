package com.achiko.backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.achiko.backend.dto.PrincipalDetails;
import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.UserRepository;
import com.achiko.backend.service.FavoriteService;
import com.achiko.backend.service.ShareService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {
    @Value("${google.api-key}")
    private String googleApiKey;

    private final ShareService shareService; 
    private final FavoriteService favoriteService;
    private final UserRepository userRepository;

    @GetMapping({"/", ""})
    public String index(
    		Model model, 
    		Principal principal,
    		@AuthenticationPrincipal PrincipalDetails loginUser
    		) {
    	
        List<ShareDTO> shareList = shareService.getShareListAll();
        boolean isLoggedIn = (principal != null);
        String isSubscribed = "";

        // 로그인한 경우에만 각 게시글의 찜 여부를 체크
        if (isLoggedIn) {
            log.info("principal={}", principal);
            UserEntity loggedUser = userRepository.findByLoginId(principal.getName());
            for (ShareDTO share : shareList) {
                boolean isFav = favoriteService.isFavorite(share.getShareId(), loggedUser.getUserId());
                share.setIsFavorite(isFav);
            }
            
            isSubscribed = loggedUser.getReceiptId();
            boolean isHost = loggedUser.getIsHost() == 1 ? true : false;
            model.addAttribute("isHost", loggedUser != null && isHost); 
        } else {
            model.addAttribute("isHost", false);  
        }

        model.addAttribute("isSubscribed", isSubscribed);
        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("shareList", shareList); 
        model.addAttribute("googleApiKey", googleApiKey);
        
        return "index"; 
    }
}
