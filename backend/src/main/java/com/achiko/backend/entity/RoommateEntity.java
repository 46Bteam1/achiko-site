package com.achiko.backend.entity;

import com.achiko.backend.dto.RoommateDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
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
@Table(name = "roommate", schema = "achiko")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoommateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roommate_id")
    private Long roommateId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "user_id", foreignKey = @ForeignKey(name = "fk_roommate_user"))
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "share_id", nullable = false, referencedColumnName = "share_id", foreignKey = @ForeignKey(name = "fk_roommate_share"))
    private ShareEntity share;

    public static RoommateEntity toEntity(RoommateDTO dto) {
        return RoommateEntity.builder()
                .roommateId(dto.getRoommateId())
                .user(UserEntity.builder().userId(dto.getUserId()).build())
                .share(ShareEntity.builder().shareId(dto.getShareId()).build())
                .build();
    }
}
