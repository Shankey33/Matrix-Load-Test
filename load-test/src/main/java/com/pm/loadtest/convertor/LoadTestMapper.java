package com.pm.loadtest.convertor;

import com.pm.common.dto.TestEventDTO;
import com.pm.loadtest.model.Test;
import org.springframework.stereotype.Component;

@Component
public class LoadTestMapper {

    public Test toEntity(TestEventDTO dto) {
        Test test = new Test();

        test.setTestId(dto.getTestId());
        test.setUsers(dto.getUsers());
        test.setSpawnRate(dto.getSpawnRate());
        test.setDurationMs(dto.getDurationMs());
        test.setQuantity(dto.getQuantity());

        test.setStatus(Test.FinalStatus.RUNNING);

        return test;
    }

    public TestEventDTO toDTO(Test test) {
        TestEventDTO dto = new TestEventDTO();

        dto.setTestId(test.getTestId());
        dto.setUsers(test.getUsers());
        dto.setSpawnRate(test.getSpawnRate());
        dto.setDurationMs(test.getDurationMs());
        dto.setQuantity(test.getQuantity());

        dto.setTotalRequests(test.getTotalRequests());
        dto.setSuccessCount(test.getSuccessCount());
        dto.setFailureCount(test.getFailureCount());
        dto.setAvgLatencyMs(test.getAvgLatencyMs());
        dto.setP95LatencyMs(test.getP95LatencyMs());
        dto.setRemainingStock(test.getRemainingStock());
        dto.setOversellDetected(test.isOversellDetected());

        if(test.getStatus() == Test.FinalStatus.PASSED){
            dto.setStatus(TestEventDTO.TestStatus.PASSED);
        }
        else if(test.getStatus() == Test.FinalStatus.RUNNING){
            dto.setStatus(TestEventDTO.TestStatus.RUNNING);
        } else {
            dto.setStatus(TestEventDTO.TestStatus.FAILED);
        }

        return dto;
    }
}
