//package com.achiko.backend.controller;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.achiko.backend.service.UserService;
//
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@Tag(name = "Admin", description = "관리자페이지 관련 API")
//@RequestMapping("/api/admin")
//@RequiredArgsConstructor
//public class AdminRestController {
//
//	 private final UserService userService; // UserService는 관리자 데이터를 제공하는 서비스
//
//	    // 대시보드 통계 API
//	    @GetMapping("/stats")
//	    public ResponseEntity<Map<String, Integer>> getDashboardStats() {
//	        Map<String, Integer> stats = new HashMap<>();
//	        stats.put("activeChatRooms", userService.getActiveChatRooms());
//	        stats.put("reportedPosts", userService.getReportedPosts());
//	        stats.put("completedViewings", userService.getCompletedViewings());
//	        return ResponseEntity.ok(stats);
//	    }
//
//	    // 사용자 모니터링 데이터 API
//	    @GetMapping("/users/monitoring")
//	    public ResponseEntity<List<UserMonitoringDTO>> getUserMonitoring() {
//	        List<UserMonitoringDTO> users = userService.getUserMonitoringData();
//	        return ResponseEntity.ok(users);
//	    }

//    // 대시보드 데이터 반환
//    @GetMapping("/api/admin/dashboard")
//    @ResponseBody
//    public AdminDashboardDTO getDashboardData() {
//        return adminService.getDashboardData();
//    }
//	
//}
