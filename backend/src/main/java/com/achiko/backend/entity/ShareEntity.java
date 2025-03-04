
package com.achiko.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Table(name = "share")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "share_id")
    private Long shareId;

    // host를 ManyToOne 관계로 UserEntity와 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private UserEntity host;

    // region, city, town을 ManyToOne 관계로 각각 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id", nullable = false)
    private ProvinceEntity province;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private RegionEntity region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private CityEntity city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "town_id", nullable = false)
    private TownEntity town;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "max_guests", nullable = false)
    private int maxGuests;

    @Column(name = "current_guests")
    private int currentGuests;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "detail_address")
    private String detailAddress;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "status")
    private String status; // "open"/"closed" 등

    public static ShareEntity fromDTO(com.achiko.backend.dto.ShareDTO dto) {
        // if (dto == null) return null;
        return ShareEntity.builder()
                .shareId(dto.getShareId())
                .host(UserEntity.builder().userId(dto.getHostId()).build())
                .province(new ProvinceEntity(dto.getProvinceId().intValue(), null, null, null))
                .region(new RegionEntity(dto.getRegionId().intValue(), null, null, null, null))
                .city(new CityEntity(dto.getCityId().intValue(), null, null, null))
                .town(new TownEntity(dto.getTownId().intValue(), null, null))
                .postalCode(dto.getPostalCode())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .maxGuests(dto.getMaxGuests())
                .currentGuests(dto.getCurrentGuests())
                .price(dto.getPrice() == null ? BigDecimal.ZERO : dto.getPrice())
                .address(dto.getAddress())
                .detailAddress(dto.getDetailAddress())
                .createdAt(dto.getCreatedAt())
                .status(dto.getStatus())
                .build();
    }
}

/*
 * @NoArgsConstructor
 * 
 * @AllArgsConstructor
 * 
 * @Setter
 * 
 * @Getter
 * 
 * @ToString
 * 
 * @Builder
 * 
 * @Entity
 * 
 * @Table(name = "share")
 */
