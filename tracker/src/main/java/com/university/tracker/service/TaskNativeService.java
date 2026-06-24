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

    // TODO (W3 review): пустой бин — день 2 AC "сервис на EntityManager напрямую" не закрыт.
    //   Реализовать методы через em (persist/find/merge/detach/flush, em.unwrap(Session.class)) или удалить класс.
}
