package com.pm.loadtest.service;

import com.pm.common.dto.BuyRequestDTO;
import com.pm.loadtest.model.Test;
import com.pm.loadtest.repository.LoadTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private final RestTemplate restTemplate;

    @Value("${loadtest.buy-url}")
    private String baseUrl;

    @Value("${loadtest.productId}")
    private String productId;

    @Async("loadTestExecutor")
    public void runTest(String testId){

        List<Long> latencies = Collections.synchronizedList(new ArrayList<>());
        Optional<Test> testModel = loadTestRepository.findById(testId);

        if(!testModel.isPresent()) {
            log.warn("Test not found when trying to fetch in RunTestAsync service!");
            return;
        };

        Test test = testModel.get();

        int users = test.getUsers();
        int poolSize = Math.min(users, 50);
        double spawnRate = test.getSpawnRate();
        long delayMs = spawnRate > 0 ? (long)(1000 / spawnRate) : 0;
        String url = baseUrl + productId;

        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failure = new AtomicInteger(0);
        AtomicLong totalLatency = new AtomicLong(0);
        log.warn(url);

        for(int i = 0; i < users; i++){
            executor.submit(() -> {
                long start = System.currentTimeMillis();
                try{

                    BuyRequestDTO request = new BuyRequestDTO();

                      ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

                    long latency = System.currentTimeMillis() - start;

                    if(response.getStatusCode().is2xxSuccessful()){
                        latencies.add(latency);
                        totalLatency.addAndGet(latency);
                        success.incrementAndGet();
                    } else {
                        failure.incrementAndGet();
                    }

                } catch (Exception e) {
                    log.error("Error simulating a req: " + e.getMessage());
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

        Collections.sort(latencies);
        int index = (int) (0.95 * latencies.size());
        long p95 = latencies.isEmpty() ? 0 : latencies.get(index);

        boolean overSell = success.get() > test.getQuantity();
        int remaining = Math.max(0, test.getQuantity() - success.get());

        test.setTotalRequests(success.get() + failure.get());
        test.setSuccessCount(success.get());
        test.setFailureCount(failure.get());
        test.setAvgLatencyMs(avgLatency);
        test.setP95LatencyMs(p95);
        test.setRemainingStock(remaining);
        test.setOversellDetected(overSell);

        if (overSell) {
            test.setStatus(Test.FinalStatus.FAILED);
        } else {
            test.setStatus(Test.FinalStatus.PASSED);
        }

        loadTestRepository.save(test);

        log.info("Stock={}, Success={}, Oversell={}", test.getQuantity(), success.get(), overSell);

    }
}
