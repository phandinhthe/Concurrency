package com.the.udemy.concurrency.coordination;

import java.math.BigInteger;

public class DaemonThread {
    public static void main(String[] args) {
        Thread thread = new Thread(new LongComputationTask(BigInteger.valueOf(2000l), BigInteger.valueOf(100000000l)));
        thread.setDaemon(true);
        thread.start();
    }

    private static class LongComputationTask implements Runnable {
        private BigInteger base;
        private BigInteger power;

        public LongComputationTask(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
            String s = String.format("%d^%d = %d", base, power, pow(base, power));
            System.out.println(s);
        }

        private BigInteger pow(BigInteger base, BigInteger power) {
            BigInteger result = BigInteger.ONE;

            for (BigInteger i = BigInteger.ZERO; i.compareTo(power) < 0; i = i.add(BigInteger.ONE)) {
                result = result.multiply(base);
            }

            return result;
        }
    }
}
