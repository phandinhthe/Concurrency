package com.the.udemy.concurrency.callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) {
        Future future = Executors.newFixedThreadPool(1)
                .submit(new FactorialTask(10));

        try {
            System.out.println(future.get());
        } catch (InterruptedException | ExecutionException ex) {
            System.out.println(ex);
        }

    }

    public static class FactorialTask implements Callable<Integer> {
        private int input;

        public FactorialTask(int input) {
            this.input = input;
        }

        @Override
        public Integer call() throws Exception {
            int rs = 1;
            for (Integer i = 1; i <= input; i++) {
                rs *= i;
            }
            return rs;
        }
    }
}
