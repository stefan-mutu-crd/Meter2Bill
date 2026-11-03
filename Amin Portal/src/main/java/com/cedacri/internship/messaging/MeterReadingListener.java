package com.cedacri.internship.messaging;

import com.cedacri.internship.configs.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class MeterReadingListener {

    @RabbitListener(queues = RabbitConfig.METER_READINGS_QUEUE)
    public void receive(Message message) {
        log.info("Meter reading received: {}", new String(message.getBody(), StandardCharsets.UTF_8));
    }
}
