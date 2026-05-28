package com.university.shelter.concurrency.race;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCounter {
    private final AtomicInteger value = new AtomicInteger();

    public void increment() {
        value.incrementAndGet(); // Измени значение с X на Y, но только если оно всё ещё равно X, lock-free подход
    }

    public int getValue() {
        return value.intValue();
    }

    public static void main(String[] args) throws InterruptedException {
        long start = System.nanoTime();

        AtomicCounter counter = new AtomicCounter();
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
