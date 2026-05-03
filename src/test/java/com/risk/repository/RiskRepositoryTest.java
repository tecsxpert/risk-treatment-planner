package com.risk.repository;

import com.risk.entity.Risk;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RiskRepositoryTest {
    @Autowired
    private RiskRepository riskRepository;

    @Test
    @DisplayName("findByStatus returns correct risks")
    void testFindByStatus() {
        Risk risk = new Risk();
        risk.setTitle("Test");
        risk.setStatus("OPEN");
        risk.setCategory("TEST_CATEGORY");
        risk.setLikelihood(1);
        risk.setImpact(1);
        riskRepository.save(risk);

        List<Risk> risks = riskRepository.findByStatus("OPEN");
        assertThat(risks).isNotEmpty();
        assertThat(risks.get(0).getStatus()).isEqualTo("OPEN");
    }
}
