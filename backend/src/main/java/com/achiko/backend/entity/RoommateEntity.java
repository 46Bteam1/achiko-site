package com.achiko.backend.entity;

import com.achiko.backend.dto.RoommateDTO;

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
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "roommate")
public class RoommateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roommate_id")
    private Long roommateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_id", referencedColumnName = "share_id")
    private ShareEntity share;

    public static RoommateEntity toEntity(RoommateDTO roommateDTO, UserEntity userEntity, ShareEntity shareEntity) {
        return RoommateEntity.builder()
                .roommateId(roommateDTO.getRoommateId())
                .user(userEntity)
                .share(shareEntity)
                .build();
    }
    
}
