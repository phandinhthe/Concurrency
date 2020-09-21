package com.the.udemy.concurrency.semaphore;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Collection<String> inputs = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        SafeQueue safeQueue = new SafeQueue();
        Thread producer = new ProducerThread(safeQueue, inputs);
        Thread consumer = new ConsumerThread(safeQueue);

        producer.start();
        consumer.start();
    }

    private static class ProducerThread extends Thread {
        private SafeQueue safeQueue;
        private Collection<String> inputs;

        public ProducerThread(SafeQueue safeQueue, Collection<String> inputs) {
            this.safeQueue = safeQueue;
            this.inputs = inputs;
        }

        @Override
        public void run() {
            for (String input : inputs) {
                safeQueue.add(input);
            }

            System.out.println("There is no more element to produce!");
            safeQueue.terminate();
            return;
        }
    }

    private static class ConsumerThread extends Thread {
        private SafeQueue safeQueue;

        public ConsumerThread(SafeQueue safeQueue) {
            this.safeQueue = safeQueue;
        }

        @Override
        public void run() {

            while (true) {
                String tmp = safeQueue.get();
                if (Objects.isNull(tmp)) {
                    break;
                }
                System.out.println("Received element " + tmp);
            }

            System.out.println("There is no more element to consume!");
        }
    }

    private static class SafeQueue {
        private Queue<String> queue = new LinkedList<>();
        private boolean isTerminated;
        private final int CAPACITY = 5;

        public synchronized void add(String element) {
            while (queue.size() == CAPACITY) {
                try {
                    wait();
                } catch (InterruptedException exception) {
                    Thread.interrupted();
                }
            }
            queue.add(element);
            notify();
        }

        public synchronized String get() {
            while (queue.isEmpty() && !isTerminated) {
                try {
                    wait();
                } catch (InterruptedException exception) {
                    Thread.interrupted();
                }
            }

            if (queue.isEmpty() && isTerminated) {
                return null;
            }

            String rs = queue.remove();
            if (CAPACITY - 1 == queue.size()) {
                notifyAll();
            }
            return rs;
        }

        public synchronized void terminate() {
            isTerminated = true;
            notifyAll();
        }
    }
}
