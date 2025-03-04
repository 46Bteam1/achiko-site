//package com.achiko.backend.controller;
//
//import java.util.List;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.achiko.backend.dto.RegionDTO;
//import com.achiko.backend.service.RegionService;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequestMapping("/api/regions")
//@RequiredArgsConstructor
//public class RegionController {
//
//    private final RegionService regionService;
//
//    /**
//     * GET /api/regions?provinceId=...
//     * provinceId에 해당하는 지역(Region) 목록을 JSON으로 반환
//     */
//    @GetMapping
//    public ResponseEntity<List<RegionDTO>> getRegions(@RequestParam("provinceId") Integer provinceId) {
//        List<RegionDTO> regions = regionService.getRegionsByProvince(provinceId);
//        return ResponseEntity.ok(regions);
//    }
//}
