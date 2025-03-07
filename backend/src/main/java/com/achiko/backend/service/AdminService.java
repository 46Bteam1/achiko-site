package com.achiko.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.repository.ReviewRepository;
import com.achiko.backend.repository.ShareRepository;
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
//    private final ReviewReplyRepository reviewReplyRepository;
//    private final ReportRepository reportRepository;

	/*
	 * @Transactional(readOnly = true) public List<UserDTO> getAllUsers() { return
	 * userRepository.findAll().stream().map(UserDTO::fromEntity).toList(); }
	 */

	/*
	 * @Transactional public void deleteUser(Long userId) {
	 * userRepository.deleteById(userId); }
	 */

    @Transactional(readOnly = true)
    public List<ShareDTO> getAllShares() {
        return shareRepository.findAll().stream().map(ShareDTO::fromEntity).toList();
    }

    @Transactional
    public void deleteShare(Long shareId) {
        shareRepository.deleteById(shareId);
    }

/*
 * @Transactional(readOnly = true) public List<ReviewDTO> getAllReviews() {
 * return
 * reviewRepository.findAll().stream().map(ReviewDTO::fromEntity).toList(); }
 * 
 * @Transactional public void deleteReview(Long reviewId) {
 * reviewRepository.deleteById(reviewId); }
 * 
 * @Transactional(readOnly = true) public List<ReportDTO> getAllReports() {
 * return
 * reportRepository.findAll().stream().map(ReportDTO::fromEntity).toList(); }
 * 
 * @Transactional public void resolveReport(Long reportId) { Report report =
 * reportRepository.findById(reportId) .orElseThrow(() -> new
 * ResourceNotFoundException("Report not found")); report.resolve();
 * reportRepository.save(report); }
 */
}