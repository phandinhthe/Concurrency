package com.the.udemy.concurrency.future;

import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
//        simpleDemo();
        TwoFutureDemo();
    }

    private static void simpleDemo() throws InterruptedException, ExecutionException, TimeoutException {
        SquareCalculator squareCalculator = new SquareCalculator();
        Future<Integer> future = squareCalculator.calculate(10);

        while(!future.isDone()) {
            System.out.println("Calculating...");
            Thread.sleep(300);
        }

        Integer result = future.get(500, TimeUnit.MILLISECONDS);
        System.out.println(result);
        shutdownAndAwaitTermination(squareCalculator.executor);
    }

    private static void TwoFutureDemo() throws ExecutionException, InterruptedException {
        SquareCalculator squareCalculator = new SquareCalculator();

        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return 100;
            }
        };
        FutureTask<Integer> future1 = new FutureTask<Integer>(callable);
        future1.get();
        System.out.println(future1.get());
//        Future<Integer> future2 = squareCalculator.calculate(100);
//
//        while (!(future1.isDone() && future2.isDone())) {
//            System.out.println(
//                    String.format(
//                            "future1 is %s and future2 is %s",
//                            future1.isDone() ? "done" : "not done",
//                            future2.isDone() ? "done" : "not done"
//                    )
//            );
//            Thread.sleep(300);
//        }
//
//        Integer result1 = future1.get();
//        Integer result2 = future2.get();
//
//        System.out.println(result1 + " and " + result2);
//
//        shutdownAndAwaitTermination(squareCalculator.executor);
    }

    static void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
                    System.out.println("pool is not terminated.");
                }
            }
        } catch (InterruptedException exception) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static class SquareCalculator {

        private ExecutorService executor
                = Executors.newFixedThreadPool(1);

        public ExecutorService getExecutor() {
            return executor;
        }

        public Future<Integer> calculate(Integer input) {
            
            return executor.submit(() -> {
                System.out.println("Calculating square for " + input);
                Thread.sleep(1000);
                return input * input;
            });
        }
    }
}
