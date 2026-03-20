package com.pm.loadtest.controller;

import com.pm.common.dto.TestEventDTO;
import com.pm.loadtest.service.LoadTestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/load-test")
@RequiredArgsConstructor
public class LoadTestController {

    private final LoadTestService loadTestService;

    @PostMapping("/start")
    public String startTest(@Valid @RequestBody TestEventDTO testEventDTO){
        return loadTestService.startTest(testEventDTO);
    }

    @GetMapping("/status/{testId}")
    public ResponseEntity<TestEventDTO> getResult(@PathVariable String testId){
        TestEventDTO result = loadTestService.getResult(testId);
        return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
    }

}
