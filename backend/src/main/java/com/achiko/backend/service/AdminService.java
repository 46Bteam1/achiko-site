package com.achiko.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.entity.ShareEntity;
import com.achiko.backend.repository.ReviewReplyRepository;
import com.achiko.backend.repository.ReviewRepository;
import com.achiko.backend.repository.ShareRepository;
import com.achiko.backend.repository.UserReportRepository;
import com.achiko.backend.repository.UserRepository;
import com.achiko.backend.repository.ViewingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
	
	private final UserRepository userRepository;
	private final ShareRepository shareRepository;
	private final ViewingRepository viewingRepository;
	private final ReviewRepository reviewRepository;
	private final ReviewReplyRepository reviewReplyRepository;
	private final UserReportRepository userReportRepository;
	
	
	public List<ShareDTO> getShareList() {
		List<ShareEntity> shareEntityList = shareRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
		List<ShareDTO> shareList = new ArrayList<>();
		shareEntityList.forEach((entity) -> shareList.add(ShareDTO.fromEntity(entity)));
		return shareList;
	}


}