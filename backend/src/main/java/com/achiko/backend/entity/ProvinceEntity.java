package com.achiko.backend.entity;

import java.util.ArrayList;
import java.util.List;

import com.achiko.backend.dto.ProvinceDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "province")
public class ProvinceEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "province_id")
	private Integer provinceId;
	
	@Column(name = "name_kanji")
	private String nameKanji;
	
	@Column(name = "name_en")
	private String nameEn;
	
    @OneToMany(mappedBy = "province", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RegionEntity> regions = new ArrayList<>();
	
    public static ProvinceEntity toEntity(ProvinceDTO dto) {
        return ProvinceEntity.builder()
                .provinceId(dto.getProvinceId())
                .nameKanji(dto.getNameKanji())
                .nameEn(dto.getNameEn())
                .build();
    }
}
