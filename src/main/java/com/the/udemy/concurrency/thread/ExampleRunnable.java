package com.the.udemy.concurrency.thread;

import java.util.Set;

public class ExampleRunnable {
    public static void main(String[] args) throws InterruptedException {
        Thread.currentThread().setName("Main Thread");

        Thread thread = new Thread(runnable);
        thread.setName("Sub Thread");
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        thread.start();
    }

    private static Runnable runnable = () -> {
        Set<Thread> threadSet =  Thread.getAllStackTraces().keySet();
        for (Thread thread : threadSet) {
            if (thread.getName().equals("Main Thread")) {
                System.out.println("The state of main thread is: " + thread.getState());
                System.out.println("Calling function getName() from sub thread..." + thread.getName());
            }
        }
        printInformation(Thread.currentThread());
        throw new RuntimeException("Caught Exception!");
    };

    private static void printInformation(Thread thread) {
        System.out.println("\n\n\nThis is thread (name - id): "
                + thread.getName().concat(" ").concat(thread.getId() + ""));

        System.out.println("Classloader is: " + thread.getContextClassLoader().toString());
        System.out.println("Priority: " + thread.getPriority());
        System.out.println("State: " + thread.getState());

    }

    private static Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (thread, throwable) -> {
        System.out.println("The thread " + thread.getName() + " has exception " + throwable.getMessage());
    };
}
