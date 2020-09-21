package com.the.udemy.concurrency.coordination;

public class BlockingThread {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(blockingTask());
        thread.start();

        Thread.sleep(1000);
        thread.interrupt();
    }

    private static Runnable blockingTask() {
        return () -> {
            try {
                while(true) {Thread.sleep(1l); System.out.println("is executing...");}
            } catch (InterruptedException exception) {
                System.out.println("Exiting interrupted task....");
            }
        };
    }
}
