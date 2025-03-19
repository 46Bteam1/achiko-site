package com.achiko.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.achiko.backend.service.TextService;
import com.achiko.backend.dto.CustomOAuth2User;
import com.achiko.backend.repository.UsersRepository;

@Controller
public class TextController {
    
    @Autowired
    private TextService textService;

    @Autowired
    private UsersRepository userRepository;
    
    @GetMapping("/find/guest-to-host")
    public String showGuestToHost(Model model) {
        String loginId = getCurrentUserLoginId();
        System.out.println("DEBUG: Retrieved loginId = " + loginId);
        if (loginId == null) {
            System.out.println("DEBUG: No logged-in user found.");
            return "redirect:/";  // Redirect if not logged in
        }

        Integer isHost = userRepository.findIsHostByLoginId(loginId);
        System.out.println("DEBUG: Retrieved isHost = " + isHost);
        if (isHost == null) {
            System.out.println("DEBUG: isHost is NULL for user " + loginId);
            return "redirect:/";
        }

        if (isHost == 1) {  // Hosts should NOT access guest-to-host recommendation
            System.out.println("DEBUG: User is a host, redirecting from 'guest-to-host'.");
            return "redirect:/";
        }

        String userBio = getCurrentUserBio();
        model.addAttribute("currentUserBio", userBio);
        return "find/guest-to-host";  // Return the correct page
    }
    @GetMapping("/find/host-to-guest")
    public String showHostToGuest(Model model) {
        String loginId = getCurrentUserLoginId();

        if (loginId == null) {
            System.out.println("DEBUG: No logged-in user found.");
            return "redirect:/";  // Redirect if not logged in
        }

        Integer isHost = userRepository.findIsHostByLoginId(loginId);

        if (isHost == null) {
            System.out.println("DEBUG: isHost is NULL for user " + loginId);
            return "redirect:/";
        }

        if (isHost == 0) {  // Guests should NOT access host-to-guest recommendation
            System.out.println("DEBUG: User is a guest, redirecting from 'host-to-guest'.");
            return "redirect:/";
        }

        String userBio = getCurrentUserBio();
        model.addAttribute("currentUserBio", userBio);
        return "find/host-to-guest";  // Return the correct page
    }
    
    @PostMapping("/calculateSimilarity/guest-to-host")
    @ResponseBody
    public Map<String, Object> calculateGuestToHostSimilarity(@RequestParam("baseText") String baseText) {
        return calculateSimilarityHelper(baseText, 1);
    }
    @PostMapping("/calculateSimilarity/host-to-guest")
    @ResponseBody
    public Map<String, Object> calculateHostToGuestSimilarity(@RequestParam("baseText") String baseText) {
        return calculateSimilarityHelper(baseText, 0);
    }
    private String getCurrentUserLoginId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            /*return userRepository.findBioByLoginId(username);*/
            return username;
        } else if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {
        	String username = ((CustomOAuth2User) authentication.getPrincipal()).getUsername();
            /*return userRepository.findBioByLoginId(username);*/
        	return username;
        }
        return null;
    }
    private Map<String, Object> calculateSimilarityHelper(String baseText, int compareWithHost) {
        String currentUserLoginId = getCurrentUserLoginId();
        String currentUserNickname = userRepository.findNicknameByLoginId(currentUserLoginId);

        Map<String, List<String>> userReputations = textService.getAllUserReputations(compareWithHost);
        Map<String, Double> averageSimilarities = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : userReputations.entrySet()) {
            String userNickname = entry.getKey();
            if (userNickname.equals(currentUserNickname)) continue;

            List<String> reputationComments = entry.getValue();
            double totalSimilarity = 0;
            int count = 0;

            for (String comment : reputationComments) {
                double similarityScore = RestClient.sendPostRequest(baseText, comment);
                totalSimilarity += similarityScore;
                count++;
            }

            double avgSimilarity = (count > 0) ? (totalSimilarity / count) * 100 : 0.0;
            averageSimilarities.put(userNickname, avgSimilarity);
        }

        // Fetch additional user details
        List<String> userNicknames = new ArrayList<>(averageSimilarities.keySet());
        Map<String, Map<String, Object>> userDetails = textService.getAllUserDetails(userNicknames);

        LinkedHashMap<String, Double> sortedSimilarities = averageSimilarities.entrySet()
                .stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .collect(LinkedHashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), Map::putAll);

        // Prepare JSON response
        Map<String, Object> response = new HashMap<>();
        response.put("similarityResults", sortedSimilarities);
        response.put("userDetails", userDetails);
        return response;
    }


    private String getCurrentUserBio() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            return userRepository.findBioByLoginId(username);
        } else if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {
        	String username = ((CustomOAuth2User) authentication.getPrincipal()).getUsername();
            return userRepository.findBioByLoginId(username);
        }
        return "No bio available";
    }
}
