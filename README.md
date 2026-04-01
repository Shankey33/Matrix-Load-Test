# Matrix Load Test

## Overview

Matrix Load Test is a distributed, event-driven microservices system designed to simulate high-throughput order processing workflows. The system models real-world scenarios such as e-commerce pipelines, emphasizing **asynchronous communication**, **fault tolerance**, and **data consistency across services**.

It incorporates advanced distributed system patterns like **SAGA orchestration**, **Redis-based optimizations**, and **resilient message handling mechanisms** to ensure robustness under load.

---

## Architecture

The system follows a **microservices-based architecture** with **event-driven communication**.

### Core Characteristics

* Asynchronous, message-driven service interaction
* Distributed transaction management using **SAGA pattern**
* High-performance caching and state handling using **Redis**
* Fault-tolerant messaging with **Poison Pill handling**
* Shared contracts via common DTO module
* Containerized infrastructure for reproducibility

---

## Services

### 1. Order Service

Handles order lifecycle through event consumption and processing.

**Key Responsibilities:**

* Consumes order-related events from the message broker
* Executes business logic for order validation and processing
* Participates in distributed transaction flow (SAGA)

**Key Components:**

* `OrderConsumer`
  Implements message consumption and orchestrates downstream actions

* `OrderServiceApplication`
  Bootstraps the Spring Boot service

---

### 2. Common Module

Defines shared data contracts across services.

**Purpose:**

* Ensures schema consistency across distributed components
* Avoids duplication of DTO definitions

**Example:**

* `StockSetDTO`
  Represents stock update payloads exchanged between services

---

## Communication Model

The system uses an **event-driven architecture** backed by a message broker.

### Flow

1. Order events are produced into the system
2. Broker distributes events to consumers
3. Consumers process events asynchronously
4. Services react independently and publish follow-up events

---

## Distributed Transaction Management (SAGA Pattern)

To maintain consistency across services without tight coupling, the system uses the **SAGA pattern**.

### Characteristics

* Each service performs a **local transaction**
* Emits events to trigger the next step in the workflow
* In case of failure, **compensating transactions** are triggered

### Benefits

* Eliminates need for distributed locking (no 2PC)
* Improves scalability and availability
* Enables fault isolation across services

---

## Redis Integration

Redis is used as a **high-performance in-memory data store** for:

* **Caching frequently accessed data** (e.g., stock state)
* **Reducing database load under high throughput**
* **Temporary state storage** during event processing
* Potential support for **idempotency keys** to prevent duplicate processing

### Impact

* Lower latency for read-heavy operations
* Improved throughput under load
* Reduced contention on persistent storage

---

## Poison Pill Handling

The system includes mechanisms to handle **Poison Pill messages** — malformed or unprocessable events that can break consumers.

### Strategy

* Detection of invalid or repeatedly failing messages
* Isolation of such messages from the main processing flow
* Prevention of consumer blocking or infinite retry loops

### Typical Handling Techniques

* Dead Letter Queue (DLQ) integration (conceptual)
* Logging and monitoring of failed payloads
* Safe skipping or quarantine of problematic messages

### Benefit

* Ensures system stability under faulty or unexpected inputs
* Prevents cascading failures in event pipelines

---

## Data Flow (Conceptual)

```id="flow1"
[Producer] → [Message Broker] → [Order Service] → [Redis/DB] → [Next Event]
```

---

## Technology Stack

* **Java**
* **Spring Boot**
* **Maven (Multi-module architecture)**
* **Redis**
* **Docker Compose**
* **Message Broker (Kafka or equivalent)**

---

## Design Patterns & Practices

* Event-Driven Architecture
* SAGA Pattern (Distributed Transactions)
* Consumer Pattern (Message Listeners)
* DTO-based Contract Sharing
* Fault-tolerant Messaging (Poison Pill Handling)
* Stateless Service Design

---

## Scalability & Reliability

* Stateless services enable horizontal scaling
* Message broker provides buffering and load leveling
* Redis reduces latency and offloads database pressure
* SAGA ensures consistency without sacrificing availability
* Poison pill handling improves resilience under failure scenarios

---

## Extensibility

The architecture allows easy addition of new services:

* Inventory Service
* Payment Service
* Notification Service

New services can subscribe to existing events without modifying current services.

---

## Repository Structure

```id="struct1"
.
├── common/                # Shared DTOs and contracts
├── order-service/         # Order processing microservice
├── docker-compose.yml     # Infrastructure setup
├── pom.xml                # Parent Maven configuration
```

---

## Key Takeaways

* Demonstrates production-grade distributed system concepts
* Implements SAGA for consistency across microservices
* Uses Redis for performance optimization
* Handles failure scenarios via poison pill strategies
* Designed for scalability, resilience, and high throughput

---
