package com.cedacri.internship.simulator;

import java.time.LocalDate;

public record SimulatedContract(Long id,
                                String serialNumber,
                                String secretKey,
                                UtilityProfile utilityType,
                                LocalDate dateStart,
                                LocalDate dateEnd) {

    public boolean isActiveOn(LocalDate date) {
        return !date.isBefore(dateStart) && !date.isAfter(dateEnd);
    }
}
