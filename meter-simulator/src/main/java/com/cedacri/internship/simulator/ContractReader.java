package com.cedacri.internship.simulator;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ContractReader {

    private final JdbcClient jdbcClient;

    public List<SimulatedContract> findActiveBetween(LocalDate from, LocalDate to) {
        return jdbcClient.sql("""
                        SELECT c.id, m.serial_number, m.secret_key, m.utility_type, c.date_start, c.date_end
                        FROM contract c
                                 JOIN meter m ON m.id = c.meter_id
                        WHERE c.date_start <= :to
                          AND c.date_end >= :from
                        """)
                .param("from", from)
                .param("to", to)
                .query(SimulatedContract.class)
                .list();
    }
}
