package com.risk.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.risk.entity.AuditLog;
import com.risk.entity.Risk;
import com.risk.repository.AuditLogRepository;
import com.risk.repository.RiskRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
public class AuditAspect {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RiskRepository riskRepository;

    // ✅ Intercept CREATE
    @Around("execution(* com.risk.service.RiskService.create(..))")
    public Object auditCreate(ProceedingJoinPoint pjp) throws Throwable {
        Object result = pjp.proceed();
        try {
            Risk created = (Risk) result;
            AuditLog log = new AuditLog();
            log.setEntityType("RISK");
            log.setEntityId(created.getId());
            log.setAction("CREATE");
            log.setChangedBy(getCurrentUser());
            log.setOldValue(null);
            log.setNewValue(objectMapper.writeValueAsString(created));
            auditLogRepository.save(log);
            System.out.println("✅ Audit logged: CREATE risk id=" + created.getId());
        } catch (Exception e) {
            System.out.println("⚠️ Audit log failed: " + e.getMessage());
        }
        return result;
    }

    // ✅ Intercept UPDATE
    @Around("execution(* com.risk.service.RiskService.update(..))")
    public Object auditUpdate(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        Long id = (Long) args[0];
        String oldValue = riskRepository.findById(id)
                .map(this::toJson)
                .orElse(null);
        Object result = pjp.proceed();
        try {
            @SuppressWarnings("unchecked")
            Optional<Risk> updated = (Optional<Risk>) result;
            String newValue = updated.map(this::toJson).orElse("not_found");
            AuditLog log = new AuditLog();
            log.setEntityType("RISK");
            log.setEntityId(id);
            log.setAction("UPDATE");
            log.setChangedBy(getCurrentUser());
            log.setOldValue(oldValue);
            log.setNewValue(newValue);
            auditLogRepository.save(log);
            System.out.println("✅ Audit logged: UPDATE risk id=" + id);
        } catch (Exception e) {
            System.out.println("⚠️ Audit log failed: " + e.getMessage());
        }
        return result;
    }

    // ✅ Intercept DELETE
    @Around("execution(* com.risk.service.RiskService.delete(..))")
    public Object auditDelete(ProceedingJoinPoint pjp) throws Throwable {
        Long id = (Long) pjp.getArgs()[0];
        Object result = pjp.proceed();
        try {
            AuditLog log = new AuditLog();
            log.setEntityType("RISK");
            log.setEntityId(id);
            log.setAction("DELETE");
            log.setChangedBy(getCurrentUser());
            log.setOldValue("id=" + id);
            log.setNewValue(null);
            auditLogRepository.save(log);
            System.out.println("✅ Audit logged: DELETE risk id=" + id);
        } catch (Exception e) {
            System.out.println("⚠️ Audit log failed: " + e.getMessage());
        }
        return result;
    }

    // ✅ Get current logged-in username
    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "system";
    }

    private String toJson(Risk risk) {
        try {
            return objectMapper.writeValueAsString(risk);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"serialize\"}";
        }
    }
}