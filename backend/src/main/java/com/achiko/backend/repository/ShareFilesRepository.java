package com.achiko.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.achiko.backend.entity.ShareFilesEntity;

@Repository
public interface ShareFilesRepository extends JpaRepository<ShareFilesEntity, Long> {

    // session_id 기준으로 임시 업로드된 파일 목록 조회 (display_order 기준 정렬)
    List<ShareFilesEntity> findBySessionIdOrderByDisplayOrderAsc(String sessionId);

    // share_id 기준으로 등록된 파일 목록 조회 (display_order 기준 정렬)
    List<ShareFilesEntity> findByShareShareIdOrderByDisplayOrderAsc(Long shareId);
}
