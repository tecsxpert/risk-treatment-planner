package com.risk.repository;

import com.risk.entity.AuditLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AuditLogRepositoryTest {
    @Autowired
    private AuditLogRepository auditLogRepository;

    @Test
    @DisplayName("findByEntityTypeAndEntityId returns correct logs")
    void testFindByEntityTypeAndEntityId() {
        AuditLog log = new AuditLog();
        log.setEntityType("USER");
        log.setEntityId(123L);
        log.setAction("CREATE");
        log.setChangedBy("tester");
        log.setOldValue(null);
        log.setNewValue("new");
        auditLogRepository.save(log);

        List<AuditLog> logs = auditLogRepository.findByEntityTypeAndEntityId("USER", 123L);
        assertThat(logs).isNotEmpty();
        assertThat(logs.get(0).getAction()).isEqualTo("CREATE");
    }
}
