package com.achiko.backend.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.achiko.backend.dto.ViewingDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity(name="viewing")
public class ViewingEntity {
	@Id
	@Column(name="viewing_id")
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long viewingId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="share_id", referencedColumnName="share_id")
	private ShareEntity share;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="guest_id", referencedColumnName="user_id")
	private UserEntity guest;
	
	@Column(name="is_completed")
	@Builder.Default
	private Boolean isCompleted = false;
	
	@Column(name="scheduled_date")
	private LocalDateTime scheduledDate;
	
	@Column(name="created_at")
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	public static ViewingEntity toEntity(ViewingDTO viewDTO, ShareEntity sEntity, UserEntity uEntity) {
		return ViewingEntity.builder()
				.viewingId(viewDTO.getViewingId())
				.share(sEntity)
				.guest(uEntity)
				.isCompleted(viewDTO.getIsCompleted())
				.scheduledDate(viewDTO.getScheduledDate())
				.build();
	}
}
	
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name="viewing_id")
//    private Long viewingId;
//	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name="share_id", nullable = false)
//    private ShareEntity share;
//	
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "guest_id", nullable = false)
//    private UserEntity guest;
//    
//    @Column(name="is_completed")
//    private Boolean isCompleted;
//    
//    @Column(name="scheduled_date")
//    private LocalDateTime scheduledDate;
//    
//    @Column(name="created_at")
//    private LocalDateTime createdAt;
//    
//    public static ViewingEntity toEntity(ViewingDTO dto, ShareEntity shareEntity, UserEntity userEntity) {
//    	return ViewingEntity.builder()
//    			.viewingId(dto.getViewingId())
//    			.share(shareEntity)
//    			.guest(userEntity)
//    			.isCompleted(dto.getIsCompleted())
//    			.scheduledDate(dto.getScheduledDate())
//    			.createdAt(dto.getCreatedAt())
//    			.build();
//    }
    

/**
 *     viewing_id int auto_increment not null primary key,
    share_id int not null,
    guest_id int not null,
    is_completed boolean default false,
    scheduled_date timestamp not null,
    created_at timestamp default current_timestamp,
*/
