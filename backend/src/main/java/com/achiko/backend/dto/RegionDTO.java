package com.achiko.backend.dto;

import com.achiko.backend.entity.RegionEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionDTO {
    private Integer id;
    private String name;
    private Integer provinceId;

    public static RegionDTO toDTO(RegionEntity entity) {
        return RegionDTO.builder()
                .id(entity.getRegionId())
                .name(entity.getNameKanji())
                .provinceId(entity.getProvinceId())
                .build();
    }
}
