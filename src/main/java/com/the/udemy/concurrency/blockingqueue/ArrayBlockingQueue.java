package com.the.udemy.concurrency.blockingqueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ArrayBlockingQueue {

    public static final int MAX_COUNT_INPUT = 1_000_000;

    public static void main(String[] args) throws InterruptedException {
        Collection<String> collection = new ArrayList<>(MAX_COUNT_INPUT);
        for (int i = 0; i < MAX_COUNT_INPUT; i++) {
            collection.add(i + "");
        }

        BlockingQueue<String> blockingQueue = new java.util.concurrent.LinkedBlockingDeque<>(5);
        SafeQueue<String> safeQueue = new SafeQueue<>(blockingQueue);
        Thread producer = new Thread(new Producer<>(safeQueue, collection));

        List<Thread> consumers = new ArrayList<>(4);
        for (int i = 0; i < 1; i++) {
            consumers.add(new Thread(new Consumer<>(safeQueue)));
        }

        long start = System.currentTimeMillis();
        consumers.forEach(Thread::start);
        producer.start();

        producer.join();
        consumers.forEach((c) -> {
            try {
                c.join();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        });

        long end = System.currentTimeMillis();
        System.out.println("Time duration: " + (end - start));
    }

    private static class SafeQueue<T> {
        private final BlockingQueue<T> queue;
        private boolean isTerminated;

        public SafeQueue(BlockingQueue<T> queue) {
            this.queue = queue;
        }

        public void put(T t) throws InterruptedException {
            queue.put(t);
        }

        public void consume() throws InterruptedException {
            T element = queue.poll(200, TimeUnit.MILLISECONDS);
            if (Objects.isNull(element)) return;
            System.out.println(" Thread" + Thread.currentThread().getName() + " is polling " + element);
        }

        public void terminate() {
            isTerminated = true;
        }

        public boolean isEmpty() {
            return queue.isEmpty();
        }
    }

    private static class Producer<T> implements Runnable {
        private final SafeQueue<T> queue;
        private final Collection<T> inputs;

        public Producer(SafeQueue<T> queue, Collection<T> inputs) {
            Thread.currentThread().setName(this.getClass().getSimpleName());
            this.queue = queue;
            this.inputs = inputs;
        }

        @Override
        public void run() {
            for (T input : inputs) {
                try {
                    queue.put(input);
                } catch (InterruptedException exception) {
                    System.out.println(Thread.currentThread().getName() + " is interrupted");
                    return;
                }
            }
            queue.terminate();
        }
    }

    private static class Consumer<T> implements Runnable {
        private final SafeQueue<T> queue;

        public Consumer(SafeQueue<T> queue) {
            Thread.currentThread().setName(this.getClass().getSimpleName());
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                if (queue.isTerminated && queue.isEmpty()) {
                    return;
                }
                try {
                    queue.consume();
                } catch (InterruptedException exception) {
                    System.out.println(Thread.currentThread().getName() + " is interrupted");
                    return;
                }
            }
        }
    }
}
