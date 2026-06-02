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

        // REVIEW[GOOD] (день 4): value++ как демонстрация гонки — это правильный выбор:
        // одна строка, а на деле три операции (read-modify-write), которые потоки перебивают.
        // REVIEW[IMPROVE]: 1_000_000 задач на 10 потоков — потеря из-за гонки (напр. 999_996)
        // почти НЕЗАМЕТНА глазами (0.0004%), а очередь executor'а раздувается. План предлагал
        // 10 потоков × 1000-10000 инкрементов — там потеря видна явно, и нет риска OOM.
        // REVIEW[BUG]: это main()-демо, а по плану нужен JUnit-тест, который assert'ит
        // "value < ожидаемого" (доказывает потерю). См. RaceConditionStressTest — его и
        // надо превратить в @Test, чтобы доказательство гонки попадало в `mvn test`.
        UnsafeCounter counter = new UnsafeCounter();
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
