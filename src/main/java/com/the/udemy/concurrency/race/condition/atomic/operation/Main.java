package com.the.udemy.concurrency.race.condition.atomic.operation;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Metric metric = new Metric();
        BusinessLogic businessLogic1 = new BusinessLogic(metric);
        BusinessLogic businessLogic2 = new BusinessLogic(metric);
        MetricPrinter metricPrinter = new MetricPrinter(metric);

        businessLogic1.start();
        businessLogic2.start();
        metricPrinter.start();
    }

    public static class MetricPrinter extends Thread {
        private Metric metric;

        public MetricPrinter(Metric metric) {
            this.metric = metric;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException exception) {
                }

                double currentAvg = metric.getAverage();
                System.out.println(String.format("Current average is: %.5f", currentAvg));
            }
        }
    }

    public static class BusinessLogic extends Thread {
        private Metric metric;
        private Random random = new Random();

        public BusinessLogic(Metric metric) {
            this.metric = metric;
        }

        @Override
        public void run() {
            while (true) {
                long start = System.currentTimeMillis();
                try {
                    Thread.sleep(random.nextInt(10));
                } catch (InterruptedException exception) {

                }
                long end = System.currentTimeMillis();
                metric.add(end - start);
            }
        }
    }

    public static class Metric {
        private long count = 0;
        private volatile double average = 0.0;

        public synchronized void add(long sample) {
            double currentSum = average * count;
            count++;
            average = (currentSum + sample) / count;
        }

        public double getAverage() {
            return this.average;
        }
    }
}
