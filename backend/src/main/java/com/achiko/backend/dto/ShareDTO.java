package com.achiko.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.achiko.backend.entity.ShareEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShareDTO {
    // DB와 1:1 매핑되는 필드
    private Long shareId;
    private Long hostId;
    private Long regionId;
    private Long cityId;
    private Long townId;
    private String postalCode;
    private String title;
    private String description;
    private int maxGuests;
    private int currentGuests;
    private BigDecimal price;
    private String address;
    private String detailAddress;
    private LocalDateTime createdAt;
    private String status;

    public static ShareDTO fromEntity(ShareEntity entity) {
        //if (entity == null) return null;
        return ShareDTO.builder()
                .shareId(entity.getShareId())
                .hostId(entity.getHost() != null ? entity.getHost().getUserId() : null)
                .regionId(entity.getRegion() != null ? Long.valueOf(entity.getRegion().getRegionId()) : null) // ★ Integer → Long 변환
                .cityId(entity.getCity() != null ? Long.valueOf(entity.getCity().getCityId()) : null)             // ★ Integer → Long 변환
                .townId(entity.getTown() != null ? Long.valueOf(entity.getTown().getTownId()) : null)             // ★ Integer → Long 변환
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
                .build();
    }
}



/*
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
 */
