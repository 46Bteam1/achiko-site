package com.achiko.backend.dto;

import com.achiko.backend.entity.TownEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TownDTO {
    private Integer id;
    private String name;
    private Integer cityId;

    /**
     * DTO → Entity 변환
     */
    public static TownDTO toDTO(TownEntity entity) {
        return TownDTO.builder()
                .id(entity.getTownId())
                .name(entity.getNameKanji())
                .cityId(entity.getCity() != null ? entity.getCity().getCityId() : null)
                .build();
    }
}
