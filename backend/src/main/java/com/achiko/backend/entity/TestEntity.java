package com.achiko.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.achiko.backend.dto.TestDTO;

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
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "test_table")
public class TestEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id; // INT AUTO_INCREMENT

	@Column(name = "bool_value")
	private Boolean boolValue; // BOOLEAN

	@Column(name = "tinyint_value")
	private Integer tinyintValue; // TINYINT

	@Column(name = "varchar_value", nullable = false, length = 100)
	private String varcharValue; // VARCHAR(100)

	@Column(name = "char_value", nullable = false, length = 10)
	private String charValue; // CHAR(10)

	@Column(name = "text_value")
	private String textValue; // TEXT

	@Column(name = "decimal_value",nullable = false, precision = 10, scale = 2)
	private BigDecimal decimalValue; // DECIMAL(10,2)

	@Column(name = "datetime_value")
	private LocalDateTime datetimeValue; // DATETIME

//	아래와 같이 insertable = false를 적고, DB에 Default current_timestamp가 있으면 @CreationTimestamp를 적지 않아도 된다. 
	@Column(name = "timestamp_value", nullable = false, updatable = false, insertable = false)
	private LocalDateTime timestampValue; // TIMESTAMP (CURRENT_TIMESTAMP)


    public static TestEntity toEntity(TestDTO dto) {
        return TestEntity.builder()
                .id(dto.getId())
                .boolValue(dto.getBoolValue())
                .tinyintValue(dto.getTinyintValue())
                .varcharValue(dto.getVarcharValue())
                .charValue(dto.getCharValue())
                .textValue(dto.getTextValue())
                .decimalValue(dto.getDecimalValue())
                .datetimeValue(dto.getDatetimeValue())
                .timestampValue(dto.getTimestampValue())
                .build();
    }
}
