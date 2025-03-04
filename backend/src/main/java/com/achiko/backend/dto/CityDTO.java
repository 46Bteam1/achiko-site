package com.achiko.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.achiko.backend.entity.CityEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityDTO {
    private Integer id;
    private String name;
    private Integer regionId;

    /**
     * DTO → Entity 변환
     */
    public static CityDTO toDTO(CityEntity entity) {
        return CityDTO.builder()
                .id(entity.getCityId())
                .name(entity.getNameKanji())
                .regionId(entity.getRegion() != null ? entity.getRegion().getRegionId() : null) 
                .build();
    }
}
