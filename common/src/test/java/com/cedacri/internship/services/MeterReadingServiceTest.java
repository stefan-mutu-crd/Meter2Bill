package com.cedacri.internship.services;

import com.cedacri.internship.documents.MeterReading;
import com.cedacri.internship.edums.UtilityType;
import com.cedacri.internship.repositories.mongo.MeterReadingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;
import tools.jackson.core.JacksonException;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class MeterReadingServiceTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongo = new MongoDBContainer("mongo:8");

    @Autowired
    private MeterReadingService meterReadingService;

    @Autowired
    private MeterReadingRepository meterReadingRepository;

    @BeforeEach
    void setUp() {
        meterReadingRepository.deleteAll();
    }

    @Test
    void saveStoresReadingFromJson() {
        String payload = """
                {"serialNumber":"EL-00001","secretKey":"secret","utilityType":"ELECTRICITY",
                 "value":1234.56,"readAt":"2026-11-03T10:00:00Z"}
                """;

        MeterReading saved = meterReadingService.save(payload);

        MeterReading stored = meterReadingRepository.findById(saved.getId()).orElseThrow();
        assertThat(stored.getSerialNumber()).isEqualTo("EL-00001");
        assertThat(stored.getUtilityType()).isEqualTo(UtilityType.ELECTRICITY);
        assertThat(stored.getValue()).isEqualByComparingTo(new BigDecimal("1234.56"));
        assertThat(stored.getReadAt()).isEqualTo(Instant.parse("2026-11-03T10:00:00Z"));
        assertThat(stored.getReceivedAt()).isNotNull();
    }

    @Test
    void saveRejectsInvalidJson() {
        assertThatThrownBy(() -> meterReadingService.save("not json")).isInstanceOf(JacksonException.class);
        assertThat(meterReadingRepository.count()).isZero();
    }
}
