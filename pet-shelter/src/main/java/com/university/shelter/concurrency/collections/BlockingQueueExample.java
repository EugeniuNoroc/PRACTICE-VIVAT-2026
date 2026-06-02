package com.university.shelter.concurrency.collections;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueueExample {
    public static void main(String[] args){
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10); // Producer (производитель) → [очередь] → Consumer (потребитель)
        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 20; i++) {
                try {
                    queue.put(i); // кладёт число, если очередь полна — ждёт
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Произвёл: " + i);
            }
            try {
                queue.put(-1);
                queue.put(-1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread consumer1 = new Thread(() -> {
            while (true) {
                Integer item = null; // берёт число, если очередь пуста — ждёт
                try {
                    item = queue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (item == -1) break;
                System.out.println("Поток номер один потребил: " + item);
            }
        });

        Thread consumer2 = new Thread(() -> {
            while (true) {
                Integer item = null; // берёт число, если очередь пуста — ждёт
                try {
                    item = queue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (item == -1) break;
                System.out.println("Поток номер два потребил: " + item);
            }
        });

        producer.start();
        consumer1.start();
        consumer2.start();
    }
}
