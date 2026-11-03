package com.cedacri.internship.simulator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.SplittableRandom;

@Component
@RequiredArgsConstructor
public class ConsumptionModel {

    private final SimulatorProperties properties;
    private final Map<Long, Position> positions = new HashMap<>();

    public BigDecimal valueAt(SimulatedContract contract, Instant time) {
        Instant hour = time.truncatedTo(ChronoUnit.HOURS);
        Position position = positions.get(contract.id());
        if (position == null || position.hour().isAfter(hour)) {
            position = new Position(contract.dateStart().atStartOfDay(properties.zone()).toInstant(), 0);
        }
        double value = position.value();
        for (Instant h = position.hour(); h.isBefore(hour); h = h.plus(1, ChronoUnit.HOURS)) {
            value += consumption(contract, h);
        }
        positions.put(contract.id(), new Position(hour, value));
        return BigDecimal.valueOf(value).setScale(3, RoundingMode.HALF_UP);
    }

    private double consumption(SimulatedContract contract, Instant hour) {
        long epochHour = hour.getEpochSecond() / 3600;
        long mix = properties.seed()
                ^ contract.serialNumber().hashCode() * 0x9E3779B97F4A7C15L
                ^ epochHour * 0xC2B2AE3D27D4EB4FL;
        double noise = 0.5 + new SplittableRandom(mix).nextDouble();
        return contract.utilityType().ratePerHour(hour.atZone(properties.zone()).toLocalDateTime()) * noise;
    }

    private record Position(Instant hour, double value) {
    }
}
