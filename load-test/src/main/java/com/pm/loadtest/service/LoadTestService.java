package com.pm.loadtest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.pm.common.dto.TestEventDTO;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoadTestService {
    private final RunTestAsync runTestAsync;

    Map<String, TestEventDTO> mpp = new ConcurrentHashMap<>();

    public String startTest(TestEventDTO testEventDTO){
        String testId = UUID.randomUUID().toString();
        int user = testEventDTO.getUsers();
        Double spawnRate = testEventDTO.getSpawnRate();
        int durationMs = testEventDTO.getDurationMs();
        int quantity = testEventDTO.getQuantity();
        testEventDTO.setStatus(TestEventDTO.TestStatus.RUNNING);
        testEventDTO.setTestId(testId);

        mpp.put(testId, testEventDTO);
        runTestAsync.runTest(testId);

        return testId;
    }


    public TestEventDTO getResult(String testId){
        if(!mpp.containsKey(testId)) return null;
        return mpp.get(testId);
    }

    public TestEventDTO getTest(String testId){
        try{
            return mpp.get(testId);
        } catch (Exception e){
            log.warn("Error retrieving the test dto for starting the test!");
        }
        return null;
    }

}
