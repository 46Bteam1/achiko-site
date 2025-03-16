package com.achiko.backend.controller;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "사용자 API")
public class SwaggerController {

    @Operation(summary = "사용자 목록 조회", description = "모든 사용자를 조회합니다.")
    @GetMapping
    public String getUsers() {
        return "사용자 목록 반환";
    }
}
