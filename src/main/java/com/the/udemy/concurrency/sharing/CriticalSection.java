package com.the.udemy.concurrency.sharing;

import java.util.concurrent.atomic.AtomicInteger;

public class CriticalSection {
    public static void main(String[] args) throws InterruptedException {
        atomicCriticalExecute();
        synchronizedCriticalExecute();
    }

    private static void atomicCriticalExecute() throws InterruptedException {
        System.out.println("=================================");
        System.out.println("=====Atomic Critical section=====");
        System.out.println();
        InventoryCounter atomicInventoryCounter = new AtomicInventoryCounter();
        IncrementingThread incrementingThread = new IncrementingThread(atomicInventoryCounter);
        DecrementingThread decrementingThread = new DecrementingThread(atomicInventoryCounter);

        long start = System.currentTimeMillis();
        incrementingThread.start();
        decrementingThread.start();

        incrementingThread.join();
        decrementingThread.join();
        long end = System.currentTimeMillis();

        System.out.println(String.format("Result: We currently have %d items", atomicInventoryCounter.getItems()));
        System.out.println(String.format("Time duration: %d", end - start));
        System.out.println("=================================\n\n");
    }

    private static void synchronizedCriticalExecute() throws InterruptedException {
        System.out.println("=================================");
        System.out.println("==Synchronized Critical section==");
        System.out.println();
        InventoryCounter synchronizedInventoryCounter = new SynchronizedInventoryCounter();
        IncrementingThread incrementingThread = new IncrementingThread(synchronizedInventoryCounter);
        DecrementingThread decrementingThread = new DecrementingThread(synchronizedInventoryCounter);

        long start = System.currentTimeMillis();
        incrementingThread.start();
        decrementingThread.start();

        incrementingThread.join();
        decrementingThread.join();
        long end = System.currentTimeMillis();

        System.out.println(String.format("Result: We currently have %d items", synchronizedInventoryCounter.getItems()));
        System.out.println(String.format("Time duration: %d", end - start));
        System.out.println("=================================\n\n");
    }

    private static class IncrementingThread extends Thread {
        private InventoryCounter inventoryCounter;

        public IncrementingThread(InventoryCounter atomicInventoryCounter) {
            this.inventoryCounter = atomicInventoryCounter;
        }

        @Override
        public void run() {
            for (int i = 0; i < 1000000; i ++) {
                this.inventoryCounter.increment();
            }
        }
    }

    private static class DecrementingThread extends Thread {
        private InventoryCounter inventoryCounter;

        public DecrementingThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for (int i = 0; i < 1000000; i ++) {
                this.inventoryCounter.decrement();
            }
        }
    }

    private static class AtomicInventoryCounter implements InventoryCounter {
        private AtomicInteger items = new AtomicInteger();

        public void increment() {
            items.getAndIncrement();
        }

        public void decrement() {
            items.decrementAndGet();
        }

        public Integer getItems() {
            return this.items.get();
        }
    }

    private static class SynchronizedInventoryCounter implements InventoryCounter {
        private Integer items = 0;

        public synchronized void increment() {
            items ++;
        }

        public synchronized void decrement() {
            items --;
        }

        public Integer getItems() {
            return this.items;
        }
    }

    private interface InventoryCounter {
        void increment();
        void decrement();
        Integer getItems();
    }


}