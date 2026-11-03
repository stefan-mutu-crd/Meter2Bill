package com.cedacri.internship.simulator;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class MeterReadingPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final JsonMapper jsonMapper;
    private final SimulatorProperties properties;

    @Bean
    public static Queue meterReadingsQueue(SimulatorProperties properties) {
        return QueueBuilder.durable(properties.queue()).build();
    }

    public void publish(SimulatedContract contract, BigDecimal value, Instant readAt) {
        MeterReadingMessage message = new MeterReadingMessage(contract.serialNumber(), contract.secretKey(),
                contract.utilityType().name(), value, readAt);
        rabbitTemplate.convertAndSend(properties.queue(), jsonMapper.writeValueAsString(message));
    }
}
