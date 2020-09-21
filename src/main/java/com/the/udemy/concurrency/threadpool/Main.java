package com.the.udemy.concurrency.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Supplier<Runnable> task = () -> () -> System.out.println(Thread.currentThread().getName());

        for (int i = 0; i < 20; i ++) {
            executorService.submit(task.get());

        }
        executorService.shutdown();
    }

    private static void shutdown(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    System.out.println("Pool is not terminating...");
                }
            }
        } catch (InterruptedException exception) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
