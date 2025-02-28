package com.achiko.backend.entity;

import java.util.ArrayList;
import java.util.List;

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
@Table(name = "city")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_id")
    private Integer cityId;

    @Column(name = "name_kanji", nullable = false)
    private String nameKanji;

    // ★ 수정: CityEntity는 ManyToOne 관계로 RegionEntity와 연결됨.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private RegionEntity region;
    
    // ★ 수정: CityEntity는 여러 TownEntity를 가질 수 있음 (OneToMany)
    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TownEntity> towns = new ArrayList<>();

    public static CityEntity toEntity(com.achiko.backend.dto.CityDTO dto) {
        CityEntity city = CityEntity.builder()
                .cityId(dto.getId())
                .nameKanji(dto.getName())
                .build();
        // ★ 임시: 연관된 RegionEntity는 dto의 regionId를 기반으로 한 임시 객체로 설정(실제 사용 시 Service에서 조회할 것)
        city.setRegion(new RegionEntity(dto.getRegionId(), null, null, null, null));
        return city;
    }

    public static com.achiko.backend.dto.CityDTO toDTO(CityEntity entity) {
        return com.achiko.backend.dto.CityDTO.builder()
                .id(entity.getCityId())
                .name(entity.getNameKanji())
                .regionId(entity.getRegion() != null ? entity.getRegion().getRegionId() : null)
                .build();
    }
}

