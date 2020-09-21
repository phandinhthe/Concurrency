package com.the.udemy.concurrency.deadlock;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        Intersection intersection = new Intersection();
        Thread thread1 = new ThreadRoadA(intersection);
        Thread thread2 = new ThreadRoadB(intersection);

        thread1.start();
        thread2.start();

    }

    private static class ThreadRoadA extends Thread {
        private Intersection intersection;

        public ThreadRoadA(Intersection intersection) {
            this.intersection = intersection;
            setName(this.getClass().getSimpleName());
        }

        @Override
        public void run() {
            while(true) {
                long sleepingTime = new Random().nextInt(5);
                try {
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException exception) {

                }
                intersection.takeRoadB();
            }
        }
    }

    private static class ThreadRoadB extends Thread {
        private Intersection intersection;

        public ThreadRoadB(Intersection intersection) {
            setName(this.getClass().getSimpleName());
            this.intersection = intersection;
        }

        @Override
        public void run() {
            while(true) {
                long sleepingTime = new Random().nextInt(5);
                try {
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException exception) {

                }
                intersection.takeRoadA();
            }
        }
    }

    private static class Intersection {
        private Object roadA = new Object();
        private Object roadB = new Object();

        public void takeRoadA() {
            synchronized (roadA) {
                System.out.println("Road A is locked by thread " + Thread.currentThread().getName());

                synchronized (roadB) {
                    System.out.println("Train is passing through road A");
                    try {
                        Thread.sleep(1l);
                    } catch (InterruptedException exception) {
                    }
                }
            }
        }

        public void takeRoadB() {
            synchronized (roadA) {
                System.out.println("Road A is locked by thread " + Thread.currentThread().getName());

                synchronized (roadB) {
                    System.out.println("Train is passing through road B");
                    try {
                        Thread.sleep(1l);
                    } catch (InterruptedException exception) {
                    }
                }
            }
        }
    }


}
