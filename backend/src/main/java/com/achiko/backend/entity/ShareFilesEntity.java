package com.achiko.backend.entity;

import java.time.LocalDateTime;

import com.achiko.backend.dto.ShareFilesDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * share_files 테이블과 매핑되는 Entity
 */
@Entity
@Table(name = "share_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShareFilesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long fileId;

    // 모집글(share)과 N:1 관계. 글 등록 전에는 null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_id")
    private ShareEntity share;

    // 임시 업로드 용 세션 식별자(등록 확정 전까지 사용)
    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "saved_file_name", nullable = false)
    private String savedFileName;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    // display_order 칼럼 추가
    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
    
    // 명시적 setter 추가 (Lombok이 생성하지 못하는 경우 대비)
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public static ShareFilesDTO toDTO(ShareFilesEntity entity) {
        if (entity == null) return null;

        return ShareFilesDTO.builder()
                .fileId(entity.getFileId())
                .shareId(entity.getShare() != null ? entity.getShare().getShareId() : null)
                .sessionId(entity.getSessionId())
                .originalFileName(entity.getOriginalFileName())
                .savedFileName(entity.getSavedFileName())
                .fileUrl(entity.getFileUrl())
                .uploadedAt(entity.getUploadedAt())
                .displayOrder(entity.getDisplayOrder())  // displayOrder 값 포함
                .build();
    }
}
