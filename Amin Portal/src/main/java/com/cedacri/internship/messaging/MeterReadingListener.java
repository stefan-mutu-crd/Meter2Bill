package com.cedacri.internship.messaging;

import com.cedacri.internship.configs.RabbitConfig;
import com.cedacri.internship.services.MeterReadingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeterReadingListener {

    private final MeterReadingService meterReadingService;

    @RabbitListener(queues = RabbitConfig.METER_READINGS_QUEUE)
    public void receive(Message message) {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("Meter reading received: {}", payload);
        meterReadingService.save(payload);
    }
}
