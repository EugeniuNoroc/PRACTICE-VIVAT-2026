package com.university.shelter.concurrency.visibility;

/**
 * REVIEW (день 4):
 * [GOOD] Комментарий про volatile/основную память по сути верный, демо чистое.
 * [IMPROVE] Сейчас показано только "как РАБОТАЕТ с volatile". Чтобы понять, ЗАЧЕМ оно,
 *   нужно увидеть БАГ без него: убери volatile и запусти — worker может НЕ остановиться
 *   (JIT закеширует running в регистр и крутит while бесконечно). Воспроизводится
 *   по-разному на разных JVM/железе — в этом и суть "нет happens-before = undefined behavior".
 *   Сделай два варианта (с/без) или оставь TODO с объяснением, что без volatile это везение.
 * [THINK] Почему пустое тело while (busy-wait) — само по себе плохо для CPU? Чем заменить,
 *   если действительно нужно ждать флаг? (подсказка: wait/notify, CountDownLatch)
 */
public class VolatileExample {
    private static volatile boolean running = true; // volatile заставляет поток main записывать значение running в основную память, и все обращения к этой переменной будут браться из основной памяти

    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            while (running) {
                // стоим на месте пока running не станет false
            }
            System.out.println("Поток остановлен");
        });

        worker.start();
        Thread.sleep(1000);
        running = false;
        System.out.println("Здесь running = false");
    }
}
