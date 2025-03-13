package com.achiko.backend.controller;

import java.security.Principal;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.achiko.backend.dto.FavoriteDTO;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.UserRepository;
import com.achiko.backend.service.FavoriteService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorite")
public class FavoriteController {

	/*
	 * private final FavoriteService favoriteService; private final UserRepository
	 * userRepository;
	 * 
	 *//**
		 * 찜하기 추가 API POST /favorite/set 요청 본문 예: { "shareId": 1 }
		 */
	/*
	 * @PostMapping("/set") public ResponseEntity<FavoriteDTO>
	 * setFavorite(@RequestBody Map<String, Long> payload, Principal principal) {
	 * Long shareId = payload.get("shareId"); if (shareId == null) { return
	 * ResponseEntity.badRequest().build(); } UserEntity user =
	 * userRepository.findByLoginId(principal.getName()); if (user == null) { return
	 * ResponseEntity.status(401).build(); } FavoriteDTO favoriteDTO =
	 * favoriteService.addFavorite(shareId, user.getUserId()); return
	 * ResponseEntity.ok(favoriteDTO); }
	 * 
	 *//**
		 * 찜 취소 API DELETE /favorite/cancel?shareId=1
		 *//*
			 * @DeleteMapping("/cancel") public ResponseEntity<Void>
			 * cancelFavorite(@RequestParam("shareId") Long shareId, Principal principal) {
			 * if (shareId == null) { return ResponseEntity.badRequest().build(); }
			 * UserEntity user = userRepository.findByLoginId(principal.getName()); if (user
			 * == null) { return ResponseEntity.status(401).build(); }
			 * favoriteService.cancelFavorite(shareId, user.getUserId()); return
			 * ResponseEntity.ok().build(); }
			 */
}
