package com.cedacri.internship.simulator;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties("simulator")
public record SimulatorProperties(LocalDate startDate,
                                  LocalDate endDate,
                                  double speedFactor,
                                  Duration tick,
                                  Duration readingInterval,
                                  long seed,
                                  ZoneId zone,
                                  String queue) {

    public Instant startInstant() {
        return startDate == null ? Instant.now().truncatedTo(ChronoUnit.HOURS) : startDate.atStartOfDay(zone).toInstant();
    }

    public Instant endInstant() {
        return endDate == null ? null : endDate.plusDays(1).atStartOfDay(zone).toInstant();
    }

    public Duration simulatedTickLength() {
        return Duration.ofMillis(Math.round(tick.toMillis() * speedFactor));
    }

    public LocalDate dateOf(Instant instant) {
        return instant.atZone(zone).toLocalDate();
    }
}
