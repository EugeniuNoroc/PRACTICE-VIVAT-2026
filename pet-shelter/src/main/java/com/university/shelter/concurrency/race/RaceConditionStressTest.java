package com.university.shelter.concurrency.race;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RaceConditionStressTest {
    public static void main(String[] args) throws InterruptedException {
        int failCount = 0;

        for(int run = 0; run < 1000; run++){
            UnsafeCounter counter = new UnsafeCounter();
            ExecutorService executor = Executors.newFixedThreadPool(10);

            for(int i = 0; i < 10000; i++) {
                executor.submit(() -> counter.increment());
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.SECONDS);

            if (counter.getValue() != 10000) {
                failCount++;
            }
        }
        System.out.println("Race condition обнаружена " + failCount + " раз из 1000");
    }
}
