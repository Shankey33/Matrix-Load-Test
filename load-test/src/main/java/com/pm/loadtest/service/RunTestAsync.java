package com.pm.loadtest.service;

import com.pm.common.dto.TestEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RunTestAsync {

    private final LoadTestService loadTestService;

    @Async("loadTestExecutor")
    public void runTest(String testId){

        TestEventDTO test = loadTestService.getTest(testId);
        if(test == null) return;

        try{
            Thread.sleep(20000);

            // simulate results
            test.setTotalRequests(1000);
            test.setSuccessCount(950);
            test.setFailureCount(50);
            test.setAvgLatencyMs(120);
            test.setP95LatencyMs(300);
            test.setRemainingStock(10);
            test.setOversellDetected(false);

            test.setStatus(TestEventDTO.TestStatus.PASSED);

        } catch (Exception e){
            log.error("Test failed! Test ID: " + testId);
            test.setStatus(TestEventDTO.TestStatus.FAILED);
        }
    }
}
