package com.pm.loadtest.service;

import com.pm.loadtest.convertor.LoadTestMapper;
import com.pm.loadtest.model.Test;
import com.pm.loadtest.repository.LoadTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RunTestAsync {

    private final LoadTestRepository loadTestRepository;
    private final LoadTestMapper loadTestMapper;

    @Async("loadTestExecutor")
    public void runTest(String testId){

        Optional<Test> testModel = loadTestRepository.findById(testId);

        if(!testModel.isPresent()) {
            log.warn("Test not found when trying to fetch in RunTestAsync service!");
            return;
        };

        Test test = testModel.get();

        int users = test.getUsers();

        int success = 0;
        int failure = 0;

        for(int i = 0; i < users; i++){
            try{
                Thread.sleep(50);

                success++;
            } catch (Exception e) {
                failure++;
            }
        }

        // simulate results
        test.setTotalRequests(users);
        test.setSuccessCount(success);
        test.setFailureCount(failure);
        test.setAvgLatencyMs(50);
        test.setP95LatencyMs(50);
        test.setRemainingStock(0);
        test.setOversellDetected(false);
        test.setStatus(Test.FinalStatus.PASSED);

        loadTestRepository.save(test);

    }
}
