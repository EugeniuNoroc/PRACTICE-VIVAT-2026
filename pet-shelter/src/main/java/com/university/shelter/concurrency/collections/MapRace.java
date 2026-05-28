package com.university.shelter.concurrency.collections;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
