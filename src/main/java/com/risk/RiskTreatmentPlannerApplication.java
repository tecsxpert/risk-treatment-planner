package com.risk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RiskTreatmentPlannerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RiskTreatmentPlannerApplication.class, args);
    }
}