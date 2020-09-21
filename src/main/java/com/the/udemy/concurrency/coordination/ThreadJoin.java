package com.the.udemy.concurrency.coordination;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThreadJoin {
    public static void main(String args[]) throws InterruptedException {
        List<Long> inputNumbers = Arrays.asList(100000000l, 3435l, 35435l, 2324l, 4656l, 23l, 5536l);
        List<FactorialThread> factorialThreads = new ArrayList<>();
        for (long inputNumber : inputNumbers) {
            factorialThreads.add(new FactorialThread(inputNumber));
        }

        for (Thread thread : factorialThreads) {
            thread.start();
        }

        for (Thread thread : factorialThreads) {
            thread.join(3000l);
            if (thread.getState() == Thread.State.TERMINATED) {
                continue;
            }
        }

        String s = null;
        for (int i = 0; i < 1; i ++) {

            FactorialThread thread = factorialThreads.get(i);
            long inputNumber = inputNumbers.get(i);
            s = thread.isFinished()
                    ? String.format("Factorial of %d is %d", inputNumber, thread.getResult())
                    : String.format("The calculation for %d is still in progress", inputNumber);
            System.out.println(s);
        }
    }

    private static class FactorialThread extends Thread {
        private long inputNumber;
        private BigInteger result;
        private volatile boolean isFinished;

        public FactorialThread(long inputNumber) {
//            setDaemon(true);
            this.inputNumber = inputNumber;
        }

        @Override
        public void run() {
            this.result = factorial(inputNumber);
            isFinished = true;
        }

        public BigInteger factorial(long input) {
            BigInteger tmpResult = BigInteger.ONE;

            for (long i = 1l; i <= input; i++) {
                tmpResult = tmpResult.multiply(BigInteger.valueOf(i));
            }

            this.result = tmpResult;
            return tmpResult;
        }

        public boolean isFinished() {
            return this.isFinished;
        }

        public BigInteger getResult() {
            return this.result;
        }
    }
}
