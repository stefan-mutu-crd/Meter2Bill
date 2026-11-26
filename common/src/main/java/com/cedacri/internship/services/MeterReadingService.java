package com.cedacri.internship.services;

import com.cedacri.internship.documents.MeterReading;
import com.cedacri.internship.repositories.mongo.MeterReadingRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeterReadingService {

    private final MeterReadingRepository meterReadingRepository;
    private final ObjectMapper jsonMapper;

    public MeterReading save(String payload) throws JsonProcessingException {
        return meterReadingRepository.save(jsonMapper.readValue(payload, MeterReading.class));
    }
}
