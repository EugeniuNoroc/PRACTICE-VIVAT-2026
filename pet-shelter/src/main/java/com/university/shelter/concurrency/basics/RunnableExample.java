package com.university.shelter.concurrency.basics;

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
