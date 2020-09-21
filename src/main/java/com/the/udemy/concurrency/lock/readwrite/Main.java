package com.the.udemy.concurrency.lock.readwrite;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Supermarket supermarket = new Supermarket(1, 1, 1);
        Loader loader = new Loader(supermarket, Integer.MAX_VALUE);
        UnLoader unLoader = new UnLoader(supermarket, Integer.MAX_VALUE);
        List<Report> reports = new ArrayList<>();
        for (int i = 0; i < 7; i ++) {
            reports.add(new Report(supermarket, 1000));
        }

        loader.start();
        unLoader.start();

        long start = System.currentTimeMillis();
        for (int i = 0; i < 7; i ++) {
            reports.get(i).start();
        }

        for (int i = 0; i < 6; i ++) {
            reports.get(i).join();
        }
        long end = System.currentTimeMillis();
        System.out.println("Duration of reading is: " + (end - start));

    }

    private static class Loader extends Thread {
        private Supermarket supermarket;
        private int count;

        public Loader(Supermarket supermarket, int count) {
            this.count = count;
            this.supermarket = supermarket;
            setDaemon(true);
        }

        @Override
        public void run() {
            for (int i = 0; i < count; i++) {
                supermarket.increment();
            }
        }
    }

    private static class UnLoader extends Thread {
        private Supermarket supermarket;
        private int count;

        public UnLoader(Supermarket supermarket, int count) {
            this.supermarket = supermarket;
            this.count = count;
            setDaemon(true);
        }

        @Override
        public void run() {
            for (int i = 0; i < count; i++) {
                supermarket.decrement();
            }
        }
    }

    private static class Report extends Thread {
        private Supermarket supermarket;
        private int count;

        public Report(Supermarket supermarket, int count) {
            this.supermarket = supermarket;
            this.count = count;
            setDaemon(true);
        }

        @Override
        public void run() {
            for (int i = 0; i < count; i++) {
                supermarket.report();
            }
        }
    }

    private static class Supermarket {
        private volatile int coca;
        private volatile int pepsi;
        private volatile int sevenUp;

        private ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
        private ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        private ReentrantReadWriteLock.ReadLock readLock = lock.readLock();

//        private Lock lock = new ReentrantLock();

        public Supermarket(int coca, int pepsi, int sevenUp) {
            this.coca = coca;
            this.pepsi = pepsi;
            this.sevenUp = sevenUp;
        }

        public void increment() {
//            lock.lock();
            writeLock.lock();
            ++coca;
            ++pepsi;
            ++sevenUp;
            writeLock.unlock();
//            lock.unlock();
        }

        public void decrement() {
//            lock.lock();
            writeLock.lock();
            --coca;
            --pepsi;
            --sevenUp;
//            lock.unlock();
            writeLock.unlock();

        }

        public void report() {
//            lock.lock();
            readLock.lock();
            try {
                Thread.sleep(1);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
//            lock.unlock();
            readLock.unlock();
        }
    }
}


