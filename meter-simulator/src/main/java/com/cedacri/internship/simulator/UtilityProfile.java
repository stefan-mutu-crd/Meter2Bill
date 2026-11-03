package com.cedacri.internship.simulator;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public enum UtilityProfile {
    ELECTRICITY(0.28, 1, 0.2, true),
    TAP_WATER(0.0055, 1, -0.1, true),
    INDUSTRIAL_WATER(0.05, 1, 0, false),
    HOT_WATER(0.0028, 1, 0.15, true),
    GAS(0.04, 1, 0.4, true),
    HEATING(0.0012, 0, 2, false);

    private static final double[] DAY_SHAPE = {
            0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.8, 1.6, 1.6, 0.9, 0.9, 0.9,
            0.9, 0.9, 0.9, 0.9, 0.9, 1.2, 1.8, 1.8, 1.8, 1.8, 1.2, 0.7};

    private final double averagePerHour;
    private final double seasonBase;
    private final double seasonAmplitude;
    private final boolean dailyPattern;

    public double ratePerHour(LocalDateTime time) {
        double winterness = Math.cos(2 * Math.PI * (time.getDayOfYear() - 15) / 365.0);
        double season = Math.max(0, seasonBase + seasonAmplitude * winterness);
        double hour = dailyPattern ? DAY_SHAPE[time.getHour()] : 1;
        return averagePerHour * season * hour;
    }
}
