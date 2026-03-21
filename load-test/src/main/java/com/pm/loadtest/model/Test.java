package com.pm.loadtest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "load_test")
@Getter @Setter
@NoArgsConstructor
public class Test {

    @Id
    private String testId;

    private int users;
    private double spawnRate;
    private int durationMs;
    private int quantity;

    private int totalRequests;
    private int successCount;
    private int failureCount;

    private long avgLatencyMs;
    private long p95LatencyMs;

    private int remainingStock;
    private boolean oversellDetected;

    @Enumerated(EnumType.STRING)
    private FinalStatus status;

    public enum FinalStatus {
        PASSED,
        RUNNING,
        FAILED
    }
}