package com.achiko.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "town")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TownEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "town_id")
    private Integer townId;

    @Column(name = "name_kanji", nullable = false)
    private String nameKanji;

    // ★ 수정: TownEntity는 ManyToOne 관계로 CityEntity와 연결됨.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private CityEntity city;

    public static TownEntity toEntity(com.achiko.backend.dto.TownDTO dto) {
        TownEntity town = TownEntity.builder()
                .townId(dto.getId())
                .nameKanji(dto.getName())
                .build();
        return town;
    }

    public static com.achiko.backend.dto.TownDTO toDTO(TownEntity entity) {
        return com.achiko.backend.dto.TownDTO.builder()
                .id(entity.getTownId())
                .name(entity.getNameKanji())
                .cityId(entity.getCity() != null ? entity.getCity().getCityId() : null)
                .build();
    }
}
