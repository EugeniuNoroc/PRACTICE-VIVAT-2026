package com.university.shelter;
import com.university.shelter.dao.CapacityDao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class CapacityDaoTest {

    private CapacityDao dao;

    @BeforeEach
    void setUp() {
        dao = new CapacityDao();
        dao.resetSpots(10); // перед каждым тестом возвращаем 10 мест
    }

    @Test
    @DisplayName("Наивная версия теряет обновления при 20 конкурентных потоках")
    void naive_shouldLoseUpdates_underConcurrency() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(20);
        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            futures.add(pool.submit(dao::tryReserveNaive));
        }

        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);

        long successCount = futures.stream()
                .filter(f -> {
                    try {
                        return f.get();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();

        int spotsLeft = dao.getCurrentSpots();

        System.out.println("=== NAIVE ===");
        System.out.println("Успешных бронирований: " + successCount);
        System.out.println("Осталось мест в БД: " + spotsLeft);
        System.out.println("Потеряно обновлений: " + (successCount - (10 - spotsLeft)));

        // Тест не проверяет assertEquals - он ДЕМОНСТРИРУЕТ проблему.
        // Если successCount > (10 - spotsLeft), значит lost update произошёл.
        assertThat(successCount).isGreaterThan(0); // просто убеждаемся что потоки работали
    }

    @Test
    @DisplayName("FOR UPDATE версия бронирует ровно 10 мест при 20 конкурентных потоках")
    void forUpdate_shouldReserveExactly10_underConcurrency() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(20);
        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            futures.add(pool.submit(dao::tryReserveForUpdate));
        }

        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);

        long successCount = futures.stream()
                .filter(f -> {
                    try {
                        return f.get();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();

        int spotsLeft = dao.getCurrentSpots();

        System.out.println("=== FOR UPDATE ===");
        System.out.println("Успешных бронирований: " + successCount);
        System.out.println("Осталось мест в БД: " + spotsLeft);

        assertThat(successCount).isEqualTo(10);
        assertThat(spotsLeft).isEqualTo(0);
    }
}