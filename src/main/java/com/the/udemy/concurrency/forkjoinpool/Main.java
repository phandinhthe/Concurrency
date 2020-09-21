package com.the.udemy.concurrency.forkjoinpool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Main {
    private static final int processorCount = Runtime.getRuntime().availableProcessors();
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool(processorCount);
        FibonaciCalculator calculator = new FibonaciCalculator(5);
        forkJoinPool.execute(calculator);
        System.out.println(calculator.get());
    }

    private static class FibonaciCalculator extends RecursiveTask<Integer> {
        private int n;

        public FibonaciCalculator(int n) {
            this.n = n;
        }

        @Override
        protected Integer compute() {
            if (n <= 2) {
                return 1;
            }

            FibonaciCalculator calculator1 = new FibonaciCalculator(n - 1);
            FibonaciCalculator calculator2 = new FibonaciCalculator(n - 2);
            calculator1.fork();
            calculator2.fork();
            return calculator1.join() + calculator2.join();
        }
    }
}
