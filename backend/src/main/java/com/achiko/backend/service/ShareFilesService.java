package com.achiko.backend.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.achiko.backend.dto.ShareFilesDTO;
import com.achiko.backend.entity.ShareEntity;
import com.achiko.backend.entity.ShareFilesEntity;
import com.achiko.backend.repository.ShareFilesRepository;
import com.achiko.backend.repository.ShareRepository;
import com.achiko.backend.util.WebPConverter;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShareFilesService {

    private final ShareFilesRepository shareFilesRepository;
    private final ShareRepository shareRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @PostConstruct
    public void checkPath() {
        System.out.println("=== uploadDir: [" + uploadDir + "]");
    }

    /**
     * 1) MultipartFile을 받아 WebP로 변환 후 임시 업로드 (share_id 없이, sessionId와 displayOrder 세팅)
     */
    @Transactional
    public ShareFilesDTO uploadFile(MultipartFile file, String sessionId, Integer displayOrder) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            originalFileName = "unknown_filename";
        }

        String uuidFileName = UUID.randomUUID().toString() + ".webp";
        Path targetPath = Paths.get(uploadDir, uuidFileName);
        File outputFile = targetPath.toFile();

        File tempFile = File.createTempFile("upload", ".tmp");
        file.transferTo(tempFile);
        WebPConverter.convertToWebP(tempFile, outputFile);

        String fileUrl = "/images/" + uuidFileName;

        ShareFilesEntity entity = ShareFilesEntity.builder()
                .sessionId(sessionId)
                .originalFileName(originalFileName)
                .savedFileName(uuidFileName)
                .fileUrl(fileUrl)
                .uploadedAt(LocalDateTime.now())
                .displayOrder(displayOrder)
                .build();

        ShareFilesEntity saved = shareFilesRepository.save(entity);

        return ShareFilesDTO.builder()
                .fileId(saved.getFileId())
                .sessionId(saved.getSessionId())
                .originalFileName(saved.getOriginalFileName())
                .savedFileName(saved.getSavedFileName())
                .fileUrl(saved.getFileUrl())
                .uploadedAt(saved.getUploadedAt())
                .displayOrder(saved.getDisplayOrder())
                .build();
    }

    /**
     * 2) ★ 파일을 ShareEntity와 연결
     */
    @Transactional
    public void bindFilesToShare(String sessionId, Long shareId) {
        List<ShareFilesEntity> tempFiles = shareFilesRepository.findBySessionIdOrderByDisplayOrderAsc(sessionId);
        ShareEntity share = shareRepository.findById(shareId)
                .orElseThrow(() -> new IllegalArgumentException("해당 Share가 존재하지 않습니다. shareId=" + shareId));

        for (ShareFilesEntity f : tempFiles) {
            f.setShare(share);
            f.setSessionId(null);
        }
        // 변경된 엔티티들을 명시적으로 저장하여 DB에 업데이트
        shareFilesRepository.saveAll(tempFiles);
    }


    /**
     * 3) 특정 shareId의 파일 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ShareFilesDTO> getFilesByShareId(Long shareId) {
        return shareFilesRepository.findByShareShareIdOrderByDisplayOrderAsc(shareId).stream()
            .map(ShareFilesEntity::toDTO)
            .toList();
    }

    /**
     * 4) 파일 삭제
     */
    @Transactional
    public void deleteFile(Long fileId) {
        ShareFilesEntity fileEntity = shareFilesRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 파일 ID입니다. fileId=" + fileId));

        String savedFileName = fileEntity.getSavedFileName();
        if (savedFileName != null) {
            File delFile = Paths.get(uploadDir, savedFileName).toFile();
            if (delFile.exists()) {
                delFile.delete();
            }
        }

        shareFilesRepository.delete(fileEntity);
    }
}
