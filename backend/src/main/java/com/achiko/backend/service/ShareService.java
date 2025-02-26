package com.achiko.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.entity.CityEntity;
import com.achiko.backend.entity.RegionEntity;
import com.achiko.backend.entity.ShareEntity;
import com.achiko.backend.entity.TownEntity;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.ShareRepository;
import com.achiko.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShareService {

    private final ShareRepository shareRepository;
    private final UserRepository userRepository;
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
                .hostId(entity.getHost() != null ? entity.getHost().getUserId() : null)  // 수정됨
                .regionId(entity.getRegion() != null ? Long.valueOf(entity.getRegion().getRegionId()) : null) // 수정됨
                .cityId(entity.getCity() != null ? Long.valueOf(entity.getCity().getCityId()) : null)         // 수정됨
                .townId(entity.getTown() != null ? Long.valueOf(entity.getTown().getTownId()) : null)         // 수정됨
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
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        ShareEntity saved = shareRepository.save(entity);
        return convertToDTO(saved);
    }

    private ShareEntity convertToEntity(ShareDTO dto) {
    	
    	if (dto.getHostId() == null) {
            throw new IllegalArgumentException("Host ID cannot be null.");
        }
    	UserEntity host = userRepository.getReferenceById(dto.getHostId());
        return ShareEntity.builder()
                .shareId(dto.getShareId())
                .host(host)
                .region(new RegionEntity(dto.getRegionId().intValue(), null, null, null, null))  // Long → int 변환
                .city(new CityEntity(dto.getCityId().intValue(), null, null, null))          // Long → int 변환
                .town(new TownEntity(dto.getTownId().intValue(), null, null))
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
}
