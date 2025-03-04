package com.achiko.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.achiko.backend.dto.CityDTO;
import com.achiko.backend.dto.TownDTO;
import com.achiko.backend.service.LocationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    /**
     * GET /api/cities?regionId=...
     * regionId에 해당하는 도시 목록을 JSON으로 반환
     */
    @GetMapping("/cities")
    public ResponseEntity<List<CityDTO>> getCities(@RequestParam("regionId") Integer regionId) {
        List<CityDTO> cities = locationService.getCitiesByRegion(regionId);
        return ResponseEntity.ok(cities);
    }

    /**
     * GET /api/towns?cityId=...
     * cityId에 해당하는 하위 지역(시/군/구) 목록을 JSON으로 반환
     */
    @GetMapping("/towns")
    public ResponseEntity<List<TownDTO>> getTowns(@RequestParam("cityId") Integer cityId) {
        List<TownDTO> towns = locationService.getTownsByCity(cityId);
        return ResponseEntity.ok(towns);
    }
}
