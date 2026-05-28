package com.university.shelter.concurrency.visibility;

public class VolatimeExample {
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
