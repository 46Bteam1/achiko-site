package com.achiko.backend.dto;

import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;
import com.achiko.backend.entity.ShareFilesEntity;
import com.achiko.backend.entity.ShareEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 이미지 파일 관련 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShareFilesDTO {

    private Long fileId;
    private Long shareId;         // ShareEntity의 shareId
    private String sessionId;     // 임시 업로드 시 사용
    private String originalFileName;
    private String savedFileName;
    private String fileUrl;
    private LocalDateTime uploadedAt;
    
    // 파일 업로드 시 활용할 MultipartFile
    private MultipartFile file;
    
    // displayOrder 필드 추가
    private Integer displayOrder;

    // DTO → Entity 변환
    public static ShareFilesEntity toEntity(ShareFilesDTO dto) {
        if (dto == null) return null;
        
        ShareFilesEntity entity = ShareFilesEntity.builder()
                .fileId(dto.getFileId())
                .sessionId(dto.getSessionId())
                .originalFileName(dto.getOriginalFileName())
                .savedFileName(dto.getSavedFileName())
                .fileUrl(dto.getFileUrl())
                .uploadedAt(dto.getUploadedAt())
                .build();
        if (dto.getShareId() != null) {
            entity.setShare(ShareEntity.builder().shareId(dto.getShareId()).build());
        }
        entity.setDisplayOrder(dto.getDisplayOrder()); // displayOrder 값 설정
        return entity;
    }
}
