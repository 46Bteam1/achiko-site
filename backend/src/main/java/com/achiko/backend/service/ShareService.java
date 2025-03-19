package com.achiko.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.achiko.backend.dto.RoommateDTO;
import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.dto.ShareFilesDTO;
import com.achiko.backend.dto.UserDTO;
import com.achiko.backend.entity.CityEntity;
import com.achiko.backend.entity.ProvinceEntity;
import com.achiko.backend.entity.RegionEntity;
import com.achiko.backend.entity.RoommateEntity;
import com.achiko.backend.entity.ShareEntity;
import com.achiko.backend.entity.TownEntity;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.CityRepository;
import com.achiko.backend.repository.ProvinceRepository;
import com.achiko.backend.repository.RegionRepository;
import com.achiko.backend.repository.ReviewRepository;
import com.achiko.backend.repository.RoommateRepository;
import com.achiko.backend.repository.ShareRepository;
import com.achiko.backend.repository.TownRepository;
import com.achiko.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShareService {

    private final ShareRepository shareRepository;
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final ProvinceRepository provinceRepository;
    private final CityRepository cityRepository;
    private final TownRepository townRepository;
    private final ShareFilesService shareFilesService;
    private final ReviewRepository reviewRepository;
    private final RatingService ratingService; 
    private final RoommateRepository roommateRepository;
    
    /**
     * 특정 shareId로 Share 조회
     */
    public ShareDTO getShareById(Long shareId) {
        Optional<ShareEntity> optionalShare = shareRepository.findById(shareId);
        log.info("+++{}", shareRepository.findById(shareId).toString());
        log.info("+++{}", optionalShare.isPresent());
        if (optionalShare.isPresent()) {
            ShareEntity entity = optionalShare.get();
            return convertToDTO(entity);
        }
        return null;
    }
  
    /**
     * 엔티티를 DTO로 변환
     */
    private ShareDTO convertToDTO(ShareEntity entity) {
        return ShareDTO.builder()
                .shareId(entity.getShareId())
                .hostId(entity.getHost() != null ? entity.getHost().getUserId() : null)
                .regionId(entity.getRegion() != null ? Long.valueOf(entity.getRegion().getRegionId()) : null)
                .cityId(entity.getCity() != null ? Long.valueOf(entity.getCity().getCityId()) : null)
                .townId(entity.getTown() != null ? Long.valueOf(entity.getTown().getTownId()) : null)
                .postalCode(entity.getPostalCode())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .maxGuests(entity.getMaxGuests())
                .currentGuests(entity.getCurrentGuests())
                .price(entity.getPrice())
                .address(entity.getAddress())
                .detailAddress(entity.getDetailAddress())
                .createdAt(entity.getCreatedAt())
                .status(entity.getStatus())
                .regionName(entity.getRegion() != null ? entity.getRegion().getNameKanji() : null)
                .cityName(entity.getCity() != null ? entity.getCity().getNameKanji() : null)
                .townName(entity.getTown() != null ? entity.getTown().getNameKanji() : null)
                .build();
    }
  
    /**
     * 등록/수정용
     */
    public ShareDTO saveShare(ShareDTO shareDTO) {
        ShareEntity entity = convertToEntity(shareDTO);
        // 글 등록 시 share의 status를 open 으로 변경
        entity.setStatus("open");
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        ShareEntity saved = shareRepository.saveAndFlush(entity);
        
        Long userId = entity.getHost().getUserId();
        Optional<UserEntity> temp = userRepository.findByUserId(userId);
        if (temp.isPresent()) {
        	UserEntity user = temp.get();
        	// 계정 유형이 '게스트'이면 이때 호스트(1)로 변경
        	if (user.getIsHost() == 0) {
        		user.setIsHost(1);
        		userRepository.save(user);
        	}
        }        
        return convertToDTO(saved);
    }
  
    /**
     * [★ 추가] 글 수정 처리 메서드
     */
    public ShareDTO updateShare(ShareDTO shareDTO) {
        ShareEntity existing = shareRepository.findById(shareDTO.getShareId())
                .orElseThrow(() -> new IllegalArgumentException("해당 글을 찾을 수 없습니다."));
        existing.setTitle(shareDTO.getTitle());
        existing.setDescription(shareDTO.getDescription());
        existing.setMaxGuests(shareDTO.getMaxGuests());
        existing.setCurrentGuests(shareDTO.getCurrentGuests());
        existing.setPostalCode(shareDTO.getPostalCode());
        existing.setAddress(shareDTO.getAddress());
        existing.setDetailAddress(shareDTO.getDetailAddress());
        existing.setPrice(shareDTO.getPrice());
        existing.setCreatedAt(LocalDateTime.now());
        
     // 위치 정보 업데이트
        if (shareDTO.getProvinceId() != null) {
            ProvinceEntity province = provinceRepository.getReferenceById(shareDTO.getProvinceId().intValue());
            existing.setProvince(province);
        }
        if (shareDTO.getRegionId() != null) {
            RegionEntity region = regionRepository.getReferenceById(shareDTO.getRegionId().intValue());
            existing.setRegion(region);
        }
        if (shareDTO.getCityId() != null) {
            CityEntity city = cityRepository.getReferenceById(shareDTO.getCityId().intValue());
            existing.setCity(city);
        }
        if (shareDTO.getTownId() != null) {
            TownEntity town = townRepository.getReferenceById(shareDTO.getTownId().intValue());
            existing.setTown(town);
        }
        
        ShareEntity updatedEntity = shareRepository.save(existing);
        return convertToDTO(updatedEntity);
    }
    
    private ShareEntity convertToEntity(ShareDTO dto) {
        if (dto.getHostId() == null) {
            throw new IllegalArgumentException("Host ID cannot be null.");
        }
        
        // Host 정보 조회
        UserEntity host = userRepository.getReferenceById(dto.getHostId());
        
        // Region 정보 조회
        RegionEntity region = regionRepository.getReferenceById(dto.getRegionId().intValue());
        
        // ProvinceEntity 생성 (ShareDTO의 provinceId를 기반으로)
        ProvinceEntity province = new ProvinceEntity(dto.getProvinceId().intValue(), null, null, null);
        
        // City, Town 정보 생성
        CityEntity city = new CityEntity(dto.getCityId().intValue(), null, null, null);
        TownEntity town = new TownEntity(dto.getTownId().intValue(), null, null);
        
        return ShareEntity.builder()
                .shareId(dto.getShareId())
                .host(host)
                .province(province)  // 추가된 부분: province 정보 설정
                .region(region)
                .city(city)
                .town(town)
                .postalCode(dto.getPostalCode())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .maxGuests(dto.getMaxGuests())
                .currentGuests(dto.getCurrentGuests())
                .price(dto.getPrice() == null ? BigDecimal.ZERO : dto.getPrice())
                .address(dto.getAddress())
                .detailAddress(dto.getDetailAddress())
                .createdAt(dto.getCreatedAt())
                .status(dto.getStatus())
                .build();
    }

  
    /**
     * [★ 삭제 기능]  
     */
    public void deleteShare(Long shareId) {
        Optional<ShareEntity> optionalShare = shareRepository.findById(shareId);
        if (!optionalShare.isPresent()) {
            throw new IllegalArgumentException("해당 게시글을 찾을 수 없습니다. shareId: " + shareId);
        }
        shareRepository.deleteById(shareId);
    }
    
    public List<ShareDTO> getShareListAll() {
        List<ShareEntity> shareEntities = shareRepository.findAll();
        List<ShareDTO> shareDTOList = new ArrayList<>();

        // 모든 share의 호스트 ID 추출 (중복 제거)
        Set<Long> hostIds = shareEntities.stream()
                .filter(entity -> entity.getHost() != null)
                .map(entity -> entity.getHost().getUserId())
                .collect(java.util.stream.Collectors.toSet());

        // RatingService를 통해 평균 평점 계산
        Map<Long, Double> avgRatingByHostId = ratingService.getAvgRatingsByHostIds(hostIds);

        for (ShareEntity entity : shareEntities) {
            ShareDTO dto = ShareDTO.fromEntity(entity);

            if (entity.getHost() != null) {
                Long hostId = entity.getHost().getUserId();
                double avgRating = avgRatingByHostId.getOrDefault(hostId, 0.0);

                if (dto.getHostDTO() == null) {
                    UserDTO hostDTO = new UserDTO();
                    hostDTO.setUserId(hostId);
                    hostDTO.setAvgRating(avgRating);
                    dto.setHostDTO(hostDTO);
                } else {
                    dto.getHostDTO().setAvgRating(avgRating);
                }
            }

            // 파일 목록 세팅
            List<ShareFilesDTO> files = shareFilesService.getFilesByShareId(entity.getShareId());
            dto.setFileList(files);
            shareDTOList.add(dto);
        }
        return shareDTOList;
    }

	public List<RoommateDTO> findRoommate(Long shareId) {
		List<RoommateEntity> entityList = roommateRepository.findByShare_ShareId(shareId);
		List<RoommateDTO> dtoList = new ArrayList<>();
		
		entityList.forEach((entity) -> dtoList.add(RoommateDTO.toDTO(entity)));
		
		return dtoList;
	}

	/**
	 * 작성한 share 글 중 status가 open인게 있다면 있다고 반환 
	 * @param userId
	 * @return
	 */
	public boolean checkAlreadyShare(Long userId) {
	    return !shareRepository.existsByHost_UserIdAndStatus(userId, "open");
	}

	public Long findShareId(String loginId) {
	    Long userId = userRepository.findByLoginId(loginId).getUserId();

	    return shareRepository.findFirstByHostUserIdAndStatus(userId, "open")
	                          .map(ShareEntity::getShareId)
	                          .orElse(null);
	}

	public Long findOpenShareId(Long userId) {
	    return shareRepository.findFirstByHostUserIdAndStatus(userId, "open")
	                          .map(ShareEntity::getShareId)
	                          .orElse(null);
	}

}
