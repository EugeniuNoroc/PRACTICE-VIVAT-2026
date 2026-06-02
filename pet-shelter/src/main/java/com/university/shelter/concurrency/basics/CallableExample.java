package com.university.shelter.concurrency.basics;

import java.util.concurrent.*;

/**
 * REVIEW (день 4):
 * [GOOD] Callable<Integer> -> submit -> future.get() -> shutdown — чистый канонический пример
 *   "задача возвращает результат". Хорошо показано отличие от Runnable (тот ничего не возвращает).
 * [THINK] future.get() здесь без таймаута. В демо ок, но в проде голый get() может зависнуть
 *   навсегда, если задача застрянет. Когда стоит использовать get(timeout, unit)?
 * [THINK] Сумму 1..1000 ты считаешь в одном потоке. А как бы ты распараллелил суммирование
 *   большого диапазона на несколько Callable и сложил результаты? (мостик к divide-and-conquer)
 */
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
