package com.university.shelter.concurrency.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * REVIEW (день 4):
 * [GOOD] 100 задач на пул из 4 потоков — по выводу (Thread.currentThread().getName())
 *   видно, что одновременно работают максимум 4 потока (pool-1-thread-1..4). Цель достигнута.
 *   Корректное завершение через shutdown() + awaitTermination().
 * [NIT] Результат awaitTermination (boolean — успели ли) игнорируется. Если за 10с не успели,
 *   ты об этом не узнаешь. Проверяй и логируй при false.
 * [THINK] int taskNum = i — зачем эта копия? Что было бы, если в лямбде использовать прямо i?
 *   (эффективно-финальные переменные в лямбдах — почему компилятор требует копию)
 */
public class FixedPoolExample {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 100; i++) {
            int taskNum = i;
            executor.submit(() -> {
                System.out.println("Task " + taskNum + " выполняется в " + Thread.currentThread().getName());
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }
}
