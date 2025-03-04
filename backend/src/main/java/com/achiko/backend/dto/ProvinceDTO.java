package com.achiko.backend.dto;

import com.achiko.backend.entity.ProvinceEntity;

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
public class ProvinceDTO {
	private Integer provinceId;
	private String nameKanji;
	private String nameEn;
	
    public static ProvinceDTO toDTO(ProvinceEntity entity) {
        return ProvinceDTO.builder()
                .provinceId(entity.getProvinceId())
                .nameKanji(entity.getNameKanji())
                .nameEn(entity.getNameEn())
                .build();
    }
}
