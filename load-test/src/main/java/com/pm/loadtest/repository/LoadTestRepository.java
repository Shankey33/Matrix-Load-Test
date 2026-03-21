package com.pm.loadtest.repository;

import com.pm.loadtest.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoadTestRepository extends JpaRepository<Test, String> {

}
