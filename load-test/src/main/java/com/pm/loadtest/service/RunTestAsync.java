package com.pm.loadtest.service;

import com.pm.loadtest.model.Test;
import com.pm.loadtest.repository.LoadTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class RunTestAsync {

    private final LoadTestRepository loadTestRepository;

    @Async("loadTestExecutor")
    public void runTest(String testId){
        
        Optional<Test> testModel = loadTestRepository.findById(testId);

        if(!testModel.isPresent()) {
            log.warn("Test not found when trying to fetch in RunTestAsync service!");
            return;
        };

        Test test = testModel.get();

        int users = test.getUsers();
        int poolSize = Math.min(users, 50);
        double spawnRate = test.getSpawnRate();
        long delayMs = (long)(1000 / spawnRate);

        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failure = new AtomicInteger(0);
        AtomicLong totalLatency = new AtomicLong(0);

        for(int i = 0; i < users; i++){
            executor.submit(() -> {
                long start = System.currentTimeMillis();
                try{
                    Thread.sleep(50);
                    long latency = System.currentTimeMillis() - start;
                    totalLatency.addAndGet(latency);
                    success.incrementAndGet();
                } catch (Exception e) {
                    failure.incrementAndGet();
                }
            });

            try{
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        executor.shutdown();

        try{
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }

        long totalReq = success.get() + failure.get();
        long avgLatency = totalReq == 0 ? 0 : totalLatency.get()/totalReq;



        test.setTotalRequests(users);
        test.setSuccessCount(success.get());
        test.setFailureCount(failure.get());
        test.setAvgLatencyMs(avgLatency);
        test.setP95LatencyMs(avgLatency);
        test.setRemainingStock(0);
        test.setOversellDetected(false);
        test.setStatus(Test.FinalStatus.PASSED);

        loadTestRepository.save(test);

    }
}
