package com.cedacri.internship.simulator;

import java.math.BigDecimal;
import java.time.Instant;

public record MeterReadingMessage(String serialNumber,
                                  String secretKey,
                                  String utilityType,
                                  BigDecimal value,
                                  Instant readAt) {
}
