package com.achiko.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.entity.CityEntity;
import com.achiko.backend.entity.ProvinceEntity;
import com.achiko.backend.entity.RegionEntity;
import com.achiko.backend.entity.ShareEntity;
import com.achiko.backend.entity.TownEntity;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.CityRepository;
import com.achiko.backend.repository.RegionRepository;
import com.achiko.backend.repository.ShareRepository;
import com.achiko.backend.repository.TownRepository;
import com.achiko.backend.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShareService {

    private final ShareRepository shareRepository;
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;
    private final TownRepository townRepository;
    
	/**
	 * Main페이지에 넣을 등록된 모든 셰어하우스 글 정보 조회 
	 */
	public List<ShareDTO> getShareListAll() {
		List<ShareEntity> shareEntityList = shareRepository.findAll();
		List<ShareDTO> shareDTOList = new ArrayList<>();
		shareEntityList.forEach((entity) -> shareDTOList.add(ShareDTO.fromEntity(entity)));
		
		return shareDTOList;
		
	}
	
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
		return ShareDTO.builder().shareId(entity.getShareId())
				.hostId(entity.getHost() != null ? entity.getHost().getUserId() : null) // 수정됨
				.regionId(entity.getRegion() != null ? Long.valueOf(entity.getRegion().getRegionId()) : null) // 수정됨
				.cityId(entity.getCity() != null ? Long.valueOf(entity.getCity().getCityId()) : null) // 수정됨
				.townId(entity.getTown() != null ? Long.valueOf(entity.getTown().getTownId()) : null) // 수정됨
				.postalCode(entity.getPostalCode()).title(entity.getTitle()).description(entity.getDescription())
				.maxGuests(entity.getMaxGuests()).currentGuests(entity.getCurrentGuests()).price(entity.getPrice())
				.address(entity.getAddress()).detailAddress(entity.getDetailAddress()).createdAt(entity.getCreatedAt())
				.status(entity.getStatus())
				.regionName(entity.getRegion() != null ? entity.getRegion().getNameKanji() : null)
				.cityName(entity.getCity() != null ? entity.getCity().getNameKanji() : null)
				.townName(entity.getTown() != null ? entity.getTown().getNameKanji() : null).build();
	}

	/**
	 * 등록/수정용
	 */
	public ShareDTO saveShare(ShareDTO shareDTO) {
		ShareEntity entity = convertToEntity(shareDTO);
		if (entity.getCreatedAt() == null) {
			entity.setCreatedAt(LocalDateTime.now());
		}
		ShareEntity saved = shareRepository.save(entity);
		return convertToDTO(saved);
	}

    /**
     * [★ 추가] 글 수정 처리 메서드
     * 기존의 ShareEntity를 찾아서 필드를 업데이트 후 저장
     */
    public ShareDTO updateShare(ShareDTO shareDTO) {
        // 기존 글을 조회
        ShareEntity existing = shareRepository.findById(shareDTO.getShareId())
                .orElseThrow(() -> new IllegalArgumentException("해당 글을 찾을 수 없습니다."));
        
        RegionEntity region = regionRepository.getReferenceById(shareDTO.getRegionId().intValue());
        CityEntity city = cityRepository.getReferenceById(shareDTO.getCityId().intValue());
        TownEntity town = townRepository.getReferenceById(shareDTO.getTownId().intValue());
        
        existing.setRegion(region);
        existing.setCity(city);
        existing.setTown(town);
        
        // 수정할 필드 업데이트 (필요한 필드 모두 업데이트)
        existing.setTitle(shareDTO.getTitle());
        existing.setDescription(shareDTO.getDescription());
        existing.setMaxGuests(shareDTO.getMaxGuests());
        existing.setCurrentGuests(shareDTO.getCurrentGuests());
        existing.setPostalCode(shareDTO.getPostalCode());
        existing.setAddress(shareDTO.getAddress());
        existing.setDetailAddress(shareDTO.getDetailAddress());
        existing.setPrice(shareDTO.getPrice());
        // 수정 시 작성일(임시) 업데이트 – 나중에 별도의 updated_at 필드를 추가할 수 있음
        existing.setCreatedAt(LocalDateTime.now());
        // 저장
        ShareEntity updatedEntity = shareRepository.save(existing);
        return convertToDTO(updatedEntity);
    }
    
    private ShareEntity convertToEntity(ShareDTO dto) {
        if (dto.getHostId() == null) {
            throw new IllegalArgumentException("Host ID cannot be null.");
        }
        UserEntity host = userRepository.getReferenceById(dto.getHostId());
        
        // ★ 기존 DB의 RegionEntity를 조회하여 사용 (provinceId 포함)
        ProvinceEntity province = new ProvinceEntity(dto.getProvinceId().intValue(), null, null, null);
        RegionEntity region = regionRepository.getReferenceById(dto.getRegionId().intValue());
        
        // CityEntity와 TownEntity는 필요에 따라 Service에서 조회하도록 개선 가능
        CityEntity city = new CityEntity(dto.getCityId().intValue(), null, null, null);
        TownEntity town = new TownEntity(dto.getTownId().intValue(), null, null);
        
        return ShareEntity.builder()
                .shareId(dto.getShareId())
                .host(host)
                .province(province)
                .region(region)  // ★ 수정된 부분: 조회한 RegionEntity 사용
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
    @Transactional
    public void deleteShare(Long shareId) {
        Optional<ShareEntity> optionalShare = shareRepository.findById(shareId);
        if (!optionalShare.isPresent()) {
            throw new IllegalArgumentException("해당 게시글을 찾을 수 없습니다. shareId: " + shareId);
        }
        ShareEntity shareEntity = optionalShare.get();
        
        // ★ 첨부 파일 삭제가 필요한 경우 참고
        // String savedFileName = shareEntity.getSavedFileName();
        // if (savedFileName != null) {
        //     String fullPath = uploadPath + "/" + savedFileName;
        //     FileService.deleteFile(fullPath);
        // }
        
        shareRepository.deleteById(shareId);
    }

}
