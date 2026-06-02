package com.university.shelter.concurrency.basics;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * REVIEW (день 4):
 * [GOOD] Bounded-очередь (capacity 10) + put/take с блокировкой — корректный producer-consumer.
 *   put ждёт, если полна; take ждёт, если пуста. Это суть BlockingQueue.
 * [NIT] -1 как "poison pill" — magic number. Что если в данных встретится -1? Обсуди
 *   альтернативы: отдельный sentinel-объект, Optional, или прерывание потока (interrupt).
 * [NIT] По плану producer-consumer относится к пакету concurrency.collections, а класс лежит
 *   в concurrency.basics. Перенеси для порядка.
 * [THINK] Здесь ровно 2 пилюли на 2 consumer'а. Что сломается, если consumer'ов станет 3?
 *   Как сделать остановку устойчивой к любому числу потребителей?
 */
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
