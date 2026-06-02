package com.university.shelter.concurrency.race;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class LockCounter {
    private int value = 0;
    private final ReentrantLock lock = new ReentrantLock();

    // REVIEW[GOOD] (день 4): lock.lock() + try/finally { unlock() } — канонический и
    // правильный паттерн. unlock именно в finally, иначе при исключении блокировка зависнет.
    public void increment() {
        lock.lock();
        try {
            value++;
        } finally {
            lock.unlock();
        }
    }

    public int getValue() {
        return value;
    }

    public static void main(String[] args) throws InterruptedException {
        long start = System.nanoTime();

        // REVIEW[BUG] (день 4): COPY-PASTE из SynchronizedCounter.main!
        // Здесь создаётся SynchronizedCounter, а НЕ LockCounter — значит бенчмарк меряет
        // synchronized, а не ReentrantLock. Сравнение lock vs synchronized сейчас
        // бессмысленно: обе цифры про synchronized. Должно быть:
        //   LockCounter counter = new LockCounter();
        // REVIEW[THINK]: ты не заметил, что цифры в SynchronizedCounter и LockCounter
        // одинаковые. Главная привычка бенчмаркинга — СМОТРЕТЬ на результат и спрашивать
        // "а почему именно так?", а не просто запускать.
        SynchronizedCounter counter = new SynchronizedCounter();
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for(int i = 0; i < 1000000; i++){
            executor.submit(() -> counter.increment());
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println(counter.getValue());

        long end = System.nanoTime();
        System.out.println("Время: " + (end - start) / 1_000_000 + "ms");
    }
}
