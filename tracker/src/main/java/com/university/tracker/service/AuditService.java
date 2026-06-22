package com.university.tracker.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuditService {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAudit(String action, UUID entityId, boolean shouldFall) {
        System.out.println("AUDIT: " + action + " FOR " + entityId);
        if (shouldFall) {
            throw new RuntimeException("AUDIT FAILED INTENTIONALLY");
        }
    }
}
