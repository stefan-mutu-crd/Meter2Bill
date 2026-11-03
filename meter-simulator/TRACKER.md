# Meter Simulator Tracker

## Purpose
Simulates the real meters: reads contracts from Postgres and publishes hourly readings to RabbitMQ `meter.readings` on a simulated clock, so months/years of data can be produced in minutes/hours (input for the upcoming Spring Batch billing).

## Stack
Spring Boot (no web), `spring-boot-starter-amqp`, `spring-boot-starter-jdbc` (`JdbcClient`), Jackson 3, Postgres driver. Does NOT depend on `common` (no JPA entities, no schema changes, no Mongo). JPA starter is inherited from the parent pom → `spring.jpa.hibernate.ddl-auto=none`.
Main: `com.cedacri.internship.simulator.MeterSimulatorApp`. Run: IntelliJ run config on the main class, or `mvn -pl meter-simulator spring-boot:run` (override props with `-Dspring-boot.run.arguments="--simulator.start-date=2026-01-01 ..."`).

## Config (`application.properties`, prefix `simulator`)
- `start-date`: empty → now (truncated to the hour); else that date 00:00 in `zone`.
- `end-date`: empty → runs until stopped; else inclusive last simulated day, then the app exits.
- `speed-factor`: simulated time = real time × factor. 1 = real time (1 reading/meter/hour). 3600 → 1 sim hour per second (1 year ≈ 2.4 h). 86400 → 1 sim day per second (1 year ≈ 6 min, publishing-bound).
- `tick`: real time between ticks (default 1s). Each tick sends all readings that fall in its simulated window.
- `reading-interval`: simulated time between readings of one meter (default 1h).
- `seed`: random seed for consumption noise (same seed → same values).
- `zone`: time zone for dates/day pattern (Europe/Chisinau). `queue`: target queue (declared durable if missing).

## How it works
- `SimulationRunner`: loop over ticks; per tick loads contracts overlapping the window (`ContractReader`, fresh each tick → contracts added in the UI are picked up), sends one reading per active contract per reading slot, sleeps until next tick (no sleep when behind → catches up).
- `ConsumptionModel`: value = cumulative consumption since contract `dateStart` 00:00, hourly increments = `UtilityProfile.ratePerHour` × deterministic noise (seed + serial + hour). Always increasing, starts at 0 on contract start, identical across runs/restarts (no state to keep). Cached position per contract id.
- `UtilityProfile`: average per hour, seasonality (cosine peaking mid-January), day shape (night low, morning/evening peaks). HEATING only Oct 16 – Apr 16. ~Monthly averages: electricity 200 kWh, tap water 4 m³, hot water 2 m³, gas 29 m³, heating up to ~1.7 Gcal in January.
- Payload = `MeterReadingMessage` JSON `{serialNumber, secretKey, utilityType, value, readAt}` (`readAt` = simulated time; billing must use `readAt`, not `receivedAt`).

## Seed data (`Amin Portal` `V6__seed_simulation_data.sql`)
5 BUILDING units (Chisinau: Dacia 21, Moscova 12, Mircea cel Batran 8, Alba Iulia 75, Ismail 46), 20 APARTMENT children each (5 floors × 4). 100 customers `customer_001`…`customer_100` (pwd `ChangeMe123!`), each OWNER of one apartment. 500 contracts (ELECTRICITY, TAP_WATER, HOT_WATER, GAS, HEATING per apartment, MONTHLY template) 2026-01-01 → 2028-12-31, each with a dedicated new meter (`EL-00051`…`EL-00150` etc., tested until 2034-12-31). V5 pool meters (`-00001`…`-00050`) stay free.

## Tests
`ConsumptionModelTest` (plain JUnit): starts at 0 and never decreases, same value regardless of history, heating off in summer.
Verified end to end on throwaway containers: V1–V6 applied on a fresh DB, 3 sim days at factor 86400 → 36,000 readings in Mongo.

## Done
- Module, simulated clock, deterministic increasing values, property file config, V6 seed data.

## Later (agreed, not done)
- Invalid readings on purpose (configurable %): wrong secretKey, unknown serial, decreasing value, duplicates, out-of-order, gaps/offline meters.
- Skip contracts of inactive contractors.
- Burst mode independent of `tick` (now: large `speed-factor` just runs behind and catches up).

## Tech debt
- Publishing ~6k msg/s; the portal listener (one Mongo insert per message, INFO log per message) consumes ~1k msg/s → at high speed factors the queue backs up (1 sim year = ~4.4M messages). Consider listener concurrency/prefetch, batch inserts, lower log level.
- Inherits JPA/validation starters from the parent pom (Hibernate boots for nothing) → move them out of the parent pom into `common`.
- Meter values start at 0 on contract start (no initial index for used meters).
- Seeded customers' names, social IDs, phones, emails are generated placeholders.
- Same local env issue as the portal: native Windows Postgres on 5432 shadows the Docker DB for `localhost`.
