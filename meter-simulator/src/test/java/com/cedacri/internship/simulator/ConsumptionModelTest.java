package com.cedacri.internship.simulator;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class ConsumptionModelTest {

    private static final ZoneId ZONE = ZoneId.of("Europe/Chisinau");
    private static final SimulatorProperties PROPERTIES = new SimulatorProperties(
            null, null, 3600, Duration.ofSeconds(1), Duration.ofHours(1), 42, ZONE, "meter.readings");

    private final SimulatedContract electricity = contract(1L, "EL-00051", UtilityProfile.ELECTRICITY);
    private final SimulatedContract heating = contract(2L, "HT-00051", UtilityProfile.HEATING);

    @Test
    void valuesStartAtZeroAndNeverDecrease() {
        ConsumptionModel model = new ConsumptionModel(PROPERTIES);
        Instant time = at(2026, 1, 1);
        BigDecimal previous = model.valueAt(electricity, time);
        assertThat(previous).isEqualByComparingTo("0");
        for (int i = 0; i < 24 * 60; i++) {
            time = time.plus(Duration.ofHours(1));
            BigDecimal current = model.valueAt(electricity, time);
            assertThat(current).isGreaterThanOrEqualTo(previous);
            previous = current;
        }
        assertThat(previous).isBetween(new BigDecimal("300"), new BigDecimal("600"));
    }

    @Test
    void sameTimeGivesSameValueRegardlessOfHistory() {
        Instant target = at(2026, 3, 10);
        ConsumptionModel stepByStep = new ConsumptionModel(PROPERTIES);
        for (Instant t = at(2026, 1, 1); t.isBefore(target); t = t.plus(Duration.ofHours(1))) {
            stepByStep.valueAt(electricity, t);
        }

        assertThat(stepByStep.valueAt(electricity, target))
                .isEqualByComparingTo(new ConsumptionModel(PROPERTIES).valueAt(electricity, target));
    }

    @Test
    void heatingIsOffInSummer() {
        ConsumptionModel model = new ConsumptionModel(PROPERTIES);

        assertThat(model.valueAt(heating, at(2026, 8, 1)))
                .isEqualByComparingTo(model.valueAt(heating, at(2026, 6, 1)));
        assertThat(model.valueAt(heating, at(2027, 2, 1)))
                .isGreaterThan(model.valueAt(heating, at(2026, 12, 1)));
    }

    private static SimulatedContract contract(Long id, String serial, UtilityProfile profile) {
        return new SimulatedContract(id, serial, "secret", profile, LocalDate.of(2026, 1, 1), LocalDate.of(2028, 12, 31));
    }

    private static Instant at(int year, int month, int day) {
        return LocalDate.of(year, month, day).atStartOfDay(ZONE).toInstant();
    }
}
