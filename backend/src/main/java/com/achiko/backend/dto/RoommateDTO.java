package com.achiko.backend.dto;

import com.achiko.backend.entity.RoommateEntity;

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
public class RoommateDTO {
    private Long roommateId;
    private Long userId;
    private Long shareId;

    public static RoommateDTO toDTO(RoommateEntity entity) {
        return RoommateDTO.builder()
                .roommateId(entity.getRoommateId())
                .userId(entity.getUser().getUserId())
                .shareId(entity.getShare().getShareId())
                .build();
    }
}