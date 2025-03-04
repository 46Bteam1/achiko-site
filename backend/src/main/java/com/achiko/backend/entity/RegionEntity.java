package com.achiko.backend.entity;

import java.util.ArrayList;
import java.util.List;

import com.achiko.backend.dto.RegionDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "region")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id")
    private Integer regionId;

    @Column(name = "name_kanji", nullable = false)
    private String nameKanji;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id", nullable = false)
    private ProvinceEntity province;
    
    // ★ 수정: RegionEntity는 여러 CityEntity를 가질 수 있음 (OneToMany)
    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CityEntity> cities = new ArrayList<>();

    // ★ 변환 메서드: DTO ⇔ Entity (단순 필드만 변환)
    public static RegionEntity toEntity(RegionDTO dto) {
    	RegionEntity region = RegionEntity.builder()
    			.regionId(dto.getId())
    			.nameKanji(dto.getName())
    			.build();
    	
    	region.setProvince(new ProvinceEntity(dto.getProvinceId(), null, null, null));
    	return region;
    }

    public static com.achiko.backend.dto.RegionDTO toDTO(RegionEntity entity) {
        return com.achiko.backend.dto.RegionDTO.builder()
                .id(entity.getRegionId())
                .name(entity.getNameKanji())
                .provinceId(entity.getProvince() != null ? entity.getProvince().getProvinceId() : null)
                .build();
    }
}
