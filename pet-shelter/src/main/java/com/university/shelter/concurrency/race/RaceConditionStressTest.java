package com.university.shelter.concurrency.race;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * REVIEW[GOOD] (день 4): сама идея "прогнать 1000 раз и поймать редкий недетерминированный
 * сбой" — правильное мышление про concurrency. Гонка проявляется не каждый раз.
 *
 * REVIEW[BUG]: awaitTermination(1, SECONDS) на 10_000 задач может НЕ успеть завершиться.
 * Тогда counter != 10000 будет не из-за гонки, а из-за обрезанного таймаута —
 * failCount накрутится false-positive'ами, и "доказательство" станет ложным.
 * Сначала ДОЖДИСЬ полного завершения (проверь boolean из awaitTermination и/или дай 30с),
 * и только потом сверяй счётчик:
 *   boolean done = executor.awaitTermination(30, TimeUnit.SECONDS);
 *   if (!done) throw new IllegalStateException("executor не успел");
 *
 * REVIEW[IMPROVE]: это должен быть JUnit-тест (@RepeatedTest или цикл + assertThat),
 * а не main(). Тогда доказательство гонки войдёт в `mvn test` и защитит от регрессий.
 */
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
