package com.university.tracker.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskNativeService {
    @PersistenceContext // Инжектирует не реальный EntityManage, а прокси
    private EntityManager em;

}
