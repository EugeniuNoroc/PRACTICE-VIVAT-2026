package com.university.shelter.concurrency.race;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SynchronizedCounter {
    private int value = 0;

    // REVIEW[GOOD] (день 4): synchronized на increment закрывает гонку read-modify-write.
    public synchronized void increment() {
        value++;
    }

    // REVIEW[THINK] (день 4): getValue НЕ synchronized и не volatile. Чтение int атомарно,
    // но без синхронизации/volatile другой поток может увидеть УСТАРЕВШЕЕ значение
    // (проблема видимости / happens-before). Здесь main делает awaitTermination перед
    // чтением, поэтому "случайно" ок. Но в общем случае это ловушка — как бы ты гарантировал
    // видимость? (synchronized getValue, либо AtomicInteger как в AtomicCounter)
    public int getValue() {
        return value;
    }

    public static void main(String[] args) throws InterruptedException {
        long start = System.nanoTime();

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
