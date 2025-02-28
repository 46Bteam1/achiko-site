package com.achiko.backend.dto;

import com.achiko.backend.entity.CityEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
