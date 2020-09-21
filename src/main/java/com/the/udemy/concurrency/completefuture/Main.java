package com.the.udemy.concurrency.completefuture;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.LongStream;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
            CompletableFuture.completedStage(LongStream.of(1l, 2l, 3l))
                    .whenComplete((stream, throwable) -> stream.forEach(System.out::println));

        Thread.sleep(5000);
    }

    private static Supplier<Order> getOrder() {
        return () -> new Order(1);
    }

    private static class Order {
        public int count = 0;

        public Order(int count) { this.count = count;}

        public Order enrichOrder() {

            System.out.println("Enrich order " + (++count));
            System.out.println(Thread.currentThread().getName());
            return this;
        }

        public Order payment() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
            System.out.println("Payment " + (++count));
            System.out.println(Thread.currentThread().getName());
            return this;
        }

        public Order sendEmail() {

            System.out.println("Send email " + (++count));
            System.out.println(Thread.currentThread().getName());
            return this;
        }
    }
}
