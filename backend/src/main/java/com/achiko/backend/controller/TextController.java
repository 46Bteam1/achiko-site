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
		if (loginId == null) {
			return "redirect:/"; // Redirect to home if user is not logged in
		}

		Integer isHost = userRepository.findIsHostByLoginId(loginId);
		if (Boolean.TRUE.equals(isHost)) { // Prevent NullPointerException
			return "redirect:/";
		}
		String userBio = getCurrentUserBio();
		model.addAttribute("currentUserBio", userBio);
		model.addAttribute("baseTexts", List.of(userBio));
		return "find/guest-to-host";
	}

	@GetMapping("/find/host-to-guest")
	public String showHostToGuest(Model model) {
		String loginId = getCurrentUserLoginId();
		if (loginId == null) {
			return "redirect:/"; // Redirect to home if user is not logged in
		}

		String userBio = getCurrentUserBio();
		model.addAttribute("currentUserBio", userBio);
		model.addAttribute("baseTexts", List.of(userBio));
		return "find/host-to-guest";
	}

	/*
	 * @PostMapping("/calculateSimilarity/guest-to-host") public String
	 * calculateGuestToHostSimilarity(@RequestParam("baseText") String baseText,
	 * Model model) { return calculateSimilarityHelper(baseText, model, 1); }
	 * 
	 * @PostMapping("/calculateSimilarity/host-to-guest") public String
	 * calculateHostToGuestSimilarity(@RequestParam("baseText") String baseText,
	 * Model model) { return calculateSimilarityHelper(baseText, model, 0); }
	 * 
	 * private String getCurrentUserLoginId() { Authentication authentication =
	 * SecurityContextHolder.getContext().getAuthentication(); if (authentication !=
	 * null && authentication.getPrincipal() instanceof UserDetails) { String
	 * username = ((UserDetails) authentication.getPrincipal()).getUsername();
	 * return userRepository.findBioByLoginId(username); } else if (authentication
	 * != null && authentication.getPrincipal() instanceof CustomOAuth2User) {
	 * String username = ((CustomOAuth2User)
	 * authentication.getPrincipal()).getUsername(); return
	 * userRepository.findBioByLoginId(username); } return null; }
	 * 
	 * private String calculateSimilarityHelper(String baseText, Model model, int
	 * compareWithHost) { String currentUserLoginId = getCurrentUserLoginId();
	 * String currentUserNickname =
	 * userRepository.findNicknameByLoginId(currentUserLoginId);
	 * 
	 * Map<String, List<String>> userReputations =
	 * textService.getAllUserReputations(compareWithHost); Map<String, Double>
	 * averageSimilarities = new HashMap<>();
	 * 
	 * for (Map.Entry<String, List<String>> entry : userReputations.entrySet()) {
	 * String userNickname = entry.getKey(); if
	 * (userNickname.equals(currentUserNickname)) continue;
	 * 
	 * List<String> reputationComments = entry.getValue(); double totalSimilarity =
	 * 0; int count = 0;
	 * 
	 * for (String comment : reputationComments) { double similarityScore =
	 * RestClient.sendPostRequest(baseText, comment); totalSimilarity +=
	 * similarityScore; count++; }
	 * 
	 * double avgSimilarity = (count > 0) ? (totalSimilarity / count) * 100 : 0.0;
	 * averageSimilarities.put(userNickname, avgSimilarity); }
	 * 
	 * // Fetch additional user details List<String> userNicknames = new
	 * ArrayList<>(averageSimilarities.keySet()); Map<String, Map<String, Object>>
	 * userDetails = textService.getAllUserDetails(userNicknames);
	 * 
	 * LinkedHashMap<String, Double> sortedSimilarities =
	 * averageSimilarities.entrySet() .stream() .sorted((e1, e2) ->
	 * Double.compare(e2.getValue(), e1.getValue())) .collect(LinkedHashMap::new,
	 * (m, v) -> m.put(v.getKey(), v.getValue()), Map::putAll);
	 * 
	 * model.addAttribute("selectedBio", baseText);
	 * model.addAttribute("similarityResults", sortedSimilarities);
	 * model.addAttribute("userDetails", userDetails);
	 * model.addAttribute("selectedMode", compareWithHost == 1 ? "guest-to-host" :
	 * "host-to-guest");
	 * 
	 * return "find/result"; }
	 */
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
			return userRepository.findBioByLoginId(username);
		} else if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {
			String username = ((CustomOAuth2User) authentication.getPrincipal()).getUsername();
			return userRepository.findBioByLoginId(username);
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
			if (userNickname.equals(currentUserNickname))
				continue;

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

		LinkedHashMap<String, Double> sortedSimilarities = averageSimilarities.entrySet().stream()
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
