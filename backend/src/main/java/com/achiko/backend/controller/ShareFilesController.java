package com.achiko.backend.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.achiko.backend.dto.ShareFilesDTO;
import com.achiko.backend.service.ShareFilesService;

import lombok.RequiredArgsConstructor;

/**
 * 파일 업로드/삭제 등 API 담당 컨트롤러
 */
@Controller
@RequiredArgsConstructor
public class ShareFilesController {

    private final ShareFilesService shareFilesService;

    /**
     * 임시 파일 업로드 (AJAX)
     */
    @PostMapping("/share-files/upload")
    @ResponseBody
    public ResponseEntity<?> uploadTempFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("sessionId") String sessionId,
            @RequestParam("displayOrder") Integer displayOrder) {
        try {
            ShareFilesDTO saved = shareFilesService.uploadFile(file, sessionId, displayOrder);
            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("파일 업로드 실패: " + e.getMessage());
        }
    }

    /**
     * 단일 파일 삭제
     */
    @PostMapping("/share-files/delete")
    @ResponseBody
    public ResponseEntity<?> deleteFile(@RequestParam("fileId") Long fileId) {
        try {
            shareFilesService.deleteFile(fileId);
            return ResponseEntity.ok("파일이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("삭제 실패: " + e.getMessage());
        }
    }
}
