package com.cedacri.internship.configs;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String METER_READINGS_QUEUE = "meter.readings";

    @Bean
    public Queue meterReadingsQueue() {
        return QueueBuilder.durable(METER_READINGS_QUEUE).build();
    }
}
