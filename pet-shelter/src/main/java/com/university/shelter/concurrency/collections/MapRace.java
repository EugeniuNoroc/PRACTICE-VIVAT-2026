package com.university.shelter.concurrency.collections;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * REVIEW (день 4):
 * [GOOD] Выбор ConcurrentHashMap показан явно, закомментированный HashMap — для контраста.
 * [IMPROVE] Сейчас активен только безопасный вариант. Чтобы УВИДЕТЬ проблему, запусти разок
 *   с HashMap: возможны потеря данных (size != 10000), вечный цикл при resize или
 *   ConcurrentModificationException. Зафиксируй наблюдение в notes — это и есть смысл демо.
 * [THINK] put(key, key) здесь по РАЗНЫМ ключам — поэтому ConcurrentHashMap полностью спасает.
 *   А спас бы он map.put(k, map.get(k) + 1) по ОДНОМУ ключу из многих потоков? (нет — почему?
 *   подсказка: get+put снова не атомарны; нужен compute / merge / atomic).
 */
public class MapRace {
    public static void main(String[] args) throws InterruptedException {
        // Map<Integer, Integer> map = new HashMap<>();
        Map<Integer, Integer> map = new ConcurrentHashMap<>();

        ExecutorService executor = Executors.newFixedThreadPool(10);
        for(int i = 0; i < 10000; i++){
            int key = i;
            executor.submit(() -> map.put(key, key));
        }
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("Size: " + map.size());
    }
}
