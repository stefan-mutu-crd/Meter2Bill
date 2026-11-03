package com.cedacri.internship.configs;

import com.cedacri.internship.repositories.mongo.MeterReadingRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackageClasses = MeterReadingRepository.class)
public class MongoConfig {
}
