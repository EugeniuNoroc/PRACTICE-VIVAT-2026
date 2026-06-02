package com.university.shelter.concurrency.race;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class UnsafeCounter {
    private int value = 0;

    public void increment() {
        value++;
    }

    public int getValue() {
        return value;
    }

    public static void main(String[] args) throws InterruptedException {
        long start = System.nanoTime();

        UnsafeCounter counter = new UnsafeCounter();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for(int i = 0; i < 1000; i++){
            executor.submit(() -> counter.increment());
        }
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println(counter.getValue());

        long end = System.nanoTime();
        System.out.println("Время: " + (end - start) / 1_000_000 + "ms");
    }
}
