package com.cedacri.internship.services;

import com.cedacri.internship.documents.MeterReading;
import com.cedacri.internship.repositories.mongo.MeterReadingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Service
@RequiredArgsConstructor
public class MeterReadingService {

    private final MeterReadingRepository meterReadingRepository;
    private final JsonMapper jsonMapper;

    public MeterReading save(String payload) {
        return meterReadingRepository.save(jsonMapper.readValue(payload, MeterReading.class));
    }
}
