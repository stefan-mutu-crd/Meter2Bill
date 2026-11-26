# Meter to Bill Project

## Overview
The **Meter to Bill** project is a distributed system designed to manage utility contracts, simulate meter readings, and process them for billing purposes. It follows a modular architecture using Spring Boot and leverages various technologies for data storage, messaging, and user interface.

## Project Structure
The project is divided into three main modules:

### 1. `common`
The core module containing shared components, business logic, and data access layers.
- **Entities & Documents**: JPA entities for relational data (PostgreSQL/H2) and MongoDB documents for meter readings.
- **Repositories**: Data access interfaces using Spring Data JPA and Spring Data MongoDB.
- **Services**: Core business services and logic reused across the system.
- **DTOs & Mappers**: Data Transfer Objects and MapStruct mappers for layer isolation.

### 2. `Amin Portal`
An administrative web application for managing the system.
- **Technology**: Built with Spring Boot and JSF (JavaServer Faces).
- **Functionality**:
    - User authentication and management.
    - Contract management (CRUD operations).
    - Monitoring of meter readings.
    - Dashboard for system overview.
- **Messaging**: Listens for meter reading events via RabbitMQ.

### 3. `meter-simulator`
A simulation tool that generates realistic meter consumption data.
- **Functionality**:
    - Reads active contracts.
    - Simulates electricity/water/gas consumption based on configurable models.
    - Publishes meter readings to RabbitMQ for processing by other services.

## Technology Stack
- **Framework**: Spring Boot 4.1.1
- **Language**: Java 17
- **Database**:
    - **Relational**: Spring Data JPA (PostgreSQL support included)
    - **NoSQL**: MongoDB (for high-volume meter readings)
- **Messaging**: RabbitMQ
- **UI**: JSF (JavaServer Faces), PrimeFaces
- **Build Tool**: Maven
- **Utilities**: Lombok, MapStruct, Jackson

## Getting Started

### Prerequisites
- JDK 17
- Maven 3.8+
- Docker (for RabbitMQ and MongoDB)

### Setup
1. Clone the repository.
2. Ensure RabbitMQ and MongoDB instances are running.
3. Build the project using Maven:
   ```bash
   mvn clean install
   ```

### Running the Applications
- **Start Amin Portal**:
  Navigate to `Amin Portal` and run `AdminPortalApp.java`.
- **Start Meter Simulator**:
  Navigate to `meter-simulator` and run `MeterSimulatorApp.java`.

## Features
- **Scalable Architecture**: Decoupled components communicating via a message broker.
- **Multi-Database Support**: Optimized storage for relational data and time-series-like readings.
- **Extensible Simulation**: Pluggable consumption models for different utility types.
- **User-Friendly Portal**: Modern web interface for administration.
