package com.cedacri.internship.repositories.mongo;

import com.cedacri.internship.documents.MeterReading;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MeterReadingRepository extends MongoRepository<MeterReading, String> {
}
