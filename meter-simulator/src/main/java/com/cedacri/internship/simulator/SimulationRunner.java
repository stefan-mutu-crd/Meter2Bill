package com.cedacri.internship.simulator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimulationRunner implements ApplicationRunner {

    private final SimulatorProperties properties;
    private final ContractReader contractReader;
    private final ConsumptionModel consumptionModel;
    private final MeterReadingPublisher publisher;

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        Instant end = properties.endInstant();
        Duration tickLength = properties.simulatedTickLength();
        Instant windowStart = properties.startInstant();
        Instant nextReading = windowStart;
        long nextTick = System.nanoTime();
        log.info("Simulation from {} to {}: 1 tick = {} real, {} simulated",
                localTime(windowStart), end == null ? "stop" : localTime(end), properties.tick(), tickLength);

        while (end == null || nextReading.isBefore(end)) {
            Instant windowEnd = end != null && windowStart.plus(tickLength).isAfter(end) ? end : windowStart.plus(tickLength);
            if (nextReading.isBefore(windowEnd)) {
                List<SimulatedContract> contracts = contractReader.findActiveBetween(
                        properties.dateOf(nextReading), properties.dateOf(windowEnd));
                int sent = 0;
                for (; nextReading.isBefore(windowEnd); nextReading = nextReading.plus(properties.readingInterval())) {
                    sent += publish(contracts, nextReading);
                }
                log.info("Simulated time {}: sent {} readings for {} contracts", localTime(windowEnd), sent, contracts.size());
            }
            windowStart = windowEnd;
            nextTick += properties.tick().toNanos();
            Thread.sleep(Math.max(0, (nextTick - System.nanoTime()) / 1_000_000));
        }
        log.info("Simulation finished at {}", localTime(end));
    }

    private int publish(List<SimulatedContract> contracts, Instant readAt) {
        LocalDate day = properties.dateOf(readAt);
        int sent = 0;
        for (SimulatedContract contract : contracts) {
            if (contract.isActiveOn(day)) {
                publisher.publish(contract, consumptionModel.valueAt(contract, readAt), readAt);
                sent++;
            }
        }
        return sent;
    }

    private LocalDateTime localTime(Instant instant) {
        return instant.atZone(properties.zone()).toLocalDateTime();
    }
}
