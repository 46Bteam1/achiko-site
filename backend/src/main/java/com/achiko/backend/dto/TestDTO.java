package com.achiko.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.achiko.backend.entity.TestEntity;

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
public class TestDTO {
    private Integer id;
    private Boolean boolValue;
    private Integer tinyintValue;
    private String varcharValue;
    private String charValue;
    private String textValue;
    private BigDecimal decimalValue;
    private LocalDateTime datetimeValue;
    private LocalDateTime timestampValue;
    
    public static TestDTO toDTO(TestEntity entity) {
        return TestDTO.builder()
                .id(entity.getId())
                .boolValue(entity.getBoolValue())
                .tinyintValue(entity.getTinyintValue())
                .varcharValue(entity.getVarcharValue())
                .charValue(entity.getCharValue())
                .textValue(entity.getTextValue())
                .decimalValue(entity.getDecimalValue())
                .datetimeValue(entity.getDatetimeValue())
                .timestampValue(entity.getTimestampValue())
                .build();
    }
}