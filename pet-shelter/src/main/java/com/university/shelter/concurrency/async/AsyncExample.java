package com.university.shelter.concurrency.async;

import java.util.concurrent.CompletableFuture;

/**
 * REVIEW (день 4):
 * [GOOD] Отличный разбор: последовательно (~1519ms) vs параллельно (~507ms), и полный
 *   pipeline supplyAsync -> thenApply -> thenCompose -> thenCombine -> exceptionally.
 *   Замеры и сохранённый "до" в комментарии — очень полезно для понимания.
 * [NIT] thenCombine(future2, ...) переиспользует future2 из верхнего блока — он УЖЕ
 *   завершён к этому моменту, так что "комбинируешь" с готовым результатом. Для чистоты
 *   демо заведи отдельный свежий CompletableFuture.
 * [THINK] Где здесь exceptionally реально сработает? Сейчас — нигде. Подмешай шаг,
 *   который бросает RuntimeException, и убедись, что ветка ошибки вызывается. Чем
 *   exceptionally отличается от handle и whenComplete?
 * [THINK] thenApply vs thenCompose — ты написал верный комментарий. А когда применяется
 *   thenApplyAsync (с суффиксом Async) и в каком пуле он выполняется?
 */
public class AsyncExample {

    /*
    private static String loadData(String source) throws InterruptedException {
        Thread.sleep(500);
        return "Данные из " + source;
    }

    public static void main(String[] args) throws InterruptedException {
        long start = System.nanoTime();

        String result1 = loadData("Источник 1");
        String result2 = loadData("Источник 2");
        String result3 = loadData("Источник 3");

        System.out.println(result1 + ", " + result2 + ", " + result3);
        System.out.println("Время: " + (System.nanoTime() - start) / 1_000_000 + "ms");
    }

    Время: 1519ms, спят каждый по очереди
    */
    public static void main(String[] args){
        long start = System.nanoTime();

        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "result-1";
        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return  "result-2";
        });
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return  "result-3";
        });

        String r1 = future1.join();
        String r2 = future2.join();
        String r3 = future3.join();
        System.out.println(r1 + ", " + r2 + ", " + r3);
        System.out.println("Время: " + (System.nanoTime() - start) / 1_000_000 + "ms");
        // Время: 507ms, спали все одновременно
        // ----------------------------------------------------------------------------------------
        CompletableFuture<String> pipeline = CompletableFuture
                .supplyAsync(() -> "data") // запуск выражения в отдельном потоке и возврат String, запуск асинхронной задачи
                .thenApply(s -> s.toUpperCase()) // s - результат прошлого шага, применяем функцию к результату, перевод текста в капс
                .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " processed")) // thenApply - принимает значение, возвращает значение, thenCompose - принимает значение, возвращает CompletableFuture<String>
                .thenCombine(future2, (s1, s2) -> s1 + " + " + s2)
                .exceptionally(ex -> "Ошибка: " + ex.getMessage());

        System.out.println(pipeline.join());
    }
}