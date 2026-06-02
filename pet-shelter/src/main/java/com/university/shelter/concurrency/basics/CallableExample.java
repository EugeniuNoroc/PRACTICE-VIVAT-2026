package com.university.shelter.concurrency.basics;

import java.util.concurrent.*;

public class CallableExample {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        Callable<Integer> task = () -> {
            int res = 0;
            for (int i = 1; i <= 1000; i++) { res = res + i; }
            return res;
        };
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(task);
        Integer result = future.get(5, TimeUnit.SECONDS);
        executor.shutdown();
        System.out.println(result);
    }
}
