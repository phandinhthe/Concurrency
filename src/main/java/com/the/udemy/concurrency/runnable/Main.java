package com.the.udemy.concurrency.runnable;

public class Main {
    public static void main(String[] args) {
        Thread thread = new Thread(new MyRunnable());
        thread.run();

        System.out.println("Hello");
    }



    private static class MyRunnable implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(5_000);
                throw new NullPointerException();
            } catch (Exception exception) {
                System.out.println(exception);
            }

        }
    }
}
