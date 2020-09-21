package com.the.udemy.concurrency.thread.state;

import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) throws InterruptedException {
//        demoStateWhenLocked();
        demoStateWhenSync();
    }

    private static void demoStateWhenSync() {
        Thread thread2;
        Thread thread1;


        SyncBusiness syncBusiness = new SyncBusiness();
        thread1 = new Thread(new MySyncTask(syncBusiness));
        thread2 = new Thread(new MySyncTask(syncBusiness));

        thread1.start();
        thread2.start();

        System.out.println("================State when synchronized======================");
        System.out.println(thread1.getState());
        System.out.println(thread2.getState());
    }

    private static void demoStateWhenLocked() {
        LockBusiness business = new LockBusiness();
        Thread thread1 = new Thread(new MyLockTask(business));
        Thread thread2 = new Thread(new MyLockTask(business));

        thread1.start();
        thread2.start();

        System.out.println("================State when locked======================");
        System.out.println(thread1.getState());
        System.out.println(thread2.getState());
    }

    private static class MyLockTask implements Runnable {
        private LockBusiness lockBusiness;

        public MyLockTask(LockBusiness lockBusiness) {
            this.lockBusiness = lockBusiness;
        }

        @Override
        public void run() {
            lockBusiness.doBusiness();
        }
    }

    private static class MySyncTask implements Runnable {
        private SyncBusiness business;

        public MySyncTask(SyncBusiness business) {
            this.business = business;
        }

        @Override
        public void run() {
            business.doBusiness();
        }
    }

    private static class LockBusiness {
        private ReentrantLock lock = new ReentrantLock();
        public void doBusiness() {
            lock.lock();
            try {
                Thread.sleep(5_000);
                System.out.println(Thread.currentThread().getName());
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
            lock.unlock();
        }
    }

    private static class SyncBusiness {
        public synchronized void doBusiness() {
            try {
                Thread.sleep(5_000);
                System.out.println(Thread.currentThread().getName());
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
    }
}
