/*package com.achiko.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Column(name = "host_id", nullable = false)
    private Long hostId;

    @Column(name = "region_id", nullable = false)
    private Long regionId;

    @Column(name = "city_id", nullable = false)
    private Long cityId;

    @Column(name = "town_id", nullable = false)
    private Long townId;

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
    private String status;  // "open"/"closed" 등


    public static ShareEntity fromDTO(ShareDTO dto) {
        //if (dto == null) return null;
        return ShareEntity.builder()
                .shareId(dto.getShareId())
                .hostId(dto.getHostId())
                .regionId(dto.getRegionId())
                .cityId(dto.getCityId())
                .townId(dto.getTownId())
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

*/
/*
 @NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder

@Entity
@Table(name = "share")
 */
