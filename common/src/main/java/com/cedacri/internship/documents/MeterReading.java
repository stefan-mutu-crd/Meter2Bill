package com.cedacri.internship.documents;

import com.cedacri.internship.edums.UtilityType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.Instant;

@Document("meter_readings")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MeterReading {

    @Id
    @Setter(AccessLevel.NONE)
    private String id;

    private String serialNumber;

    private UtilityType utilityType;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal value;

    private Instant readAt;

    @CreatedDate
    private Instant receivedAt;
}
