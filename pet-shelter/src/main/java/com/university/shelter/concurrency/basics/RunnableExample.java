package com.university.shelter.concurrency.basics;

/**
 * REVIEW (день 4):
 * [GOOD] Три потока, видно interleaving вывода — цель демо достигнута.
 * [NIT] Три почти идентичных Runnable (t/z/x) — копипаста. Можно сделать фабрику:
 *   Runnable task(String name) { return () -> { ... } } и создать 3 потока в цикле.
 * [THINK] В catch стоит throw new RuntimeException(e) на InterruptedException. Это
 *   "проглатывает" сам факт прерывания. Что правильнее делать с InterruptedException?
 *   (подсказка: Thread.currentThread().interrupt() — восстановить флаг прерывания)
 * [THINK] Порядок вывода потоков недетерминирован и Thread.sleep(100) его НЕ гарантирует.
 *   Почему sleep — это не инструмент синхронизации?
 */
public class RunnableExample {
    public static void main(String[] args) {

        Thread t = new Thread(() -> {
            for(int i = 0; i < 5; i++){
                System.out.println("My name is first thread");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread z = new Thread(() -> {
            for(int i = 0; i < 5; i++){
                System.out.println("My name is second thread");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread x = new Thread(() -> {
            for(int i = 0; i < 5; i++){
                System.out.println("My name is third thread");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        t.start();
        x.start();
        z.start();
    }
}
