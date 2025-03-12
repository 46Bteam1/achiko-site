package com.achiko.backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.UserRepository;
import com.achiko.backend.service.FavoriteService;
import com.achiko.backend.service.ShareService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {
    @Value("${google.api-key}")
    private String googleApiKey;

    private final ShareService shareService; 
    private final FavoriteService favoriteService;
    private final UserRepository userRepository;

    @GetMapping({"/", ""})
    public String index(Model model, Principal principal) {
        List<ShareDTO> shareList = shareService.getShareListAll();

        // 로그인한 경우, 각 게시글의 찜 여부를 체크
        if (principal != null) {
            UserEntity loggedUser = userRepository.findByLoginId(principal.getName());
            for (ShareDTO share : shareList) {
                boolean isFav = favoriteService.isFavorite(share.getShareId(), loggedUser.getUserId());
                share.setIsFavorite(isFav);
            }
        }
        model.addAttribute("shareList", shareList); 
        model.addAttribute("googleApiKey", googleApiKey);
        return "index"; 
    }
}
