package com.the.udemy.concurrency.threadlocal;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {

        Runnable task1 = new MyTask(ContextGenerator.getInstance().generate());
        Runnable task2 = new MyTask(ContextGenerator.getInstance().generate());
        Runnable task3 = new MyTask(ContextGenerator.getInstance().generate());
        Runnable task4 = new MyTask(ContextGenerator.getInstance().generate());

        ExecutorService executorService =  Executors.newFixedThreadPool(2);

        Future<?> future1 = executorService.submit(task1);
        Future<?> future2 = executorService.submit(task2);
        Future<?> future3 = executorService.submit(task3);
        Future<?> future4 = executorService.submit(task4);
        try {
            future1.get();
            future2.get();
            future3.get();
            future4.get();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        shutdown(executorService);
    }

    private static void shutdown(ExecutorService executorService) {
        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }

            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                System.out.println("Pool does not terminating!");
            }

        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    private static class Context {
        private String name;
        private String uid;

        private Context(String name, String uid) {
            this.name = name;
            this.uid = uid;
        }

        public static Context createSimpleContext(String name, String uid) {
            return new Context(name, uid);
        }
    }

    private static class ContextGenerator {
        private static Optional<ContextGenerator> instance = Optional.empty();
        private static final Object lock = new Object();
        private static final Random rnd =  new Random();

        private ContextGenerator() {
        }

        public static ContextGenerator getInstance() {
            if (instance.isPresent()) {
                return instance.get();
            }
            return get.apply(instance);
        }

        private static Function<Optional<ContextGenerator>, ContextGenerator> get = (optional) -> {
            if (optional.isPresent()) {
                return instance.get();
            }
            synchronized (lock) {
                if (optional.isEmpty()) {
                    instance = Optional.of(new ContextGenerator());
                }
            }
            return instance.get();
        };

        public Context generate() {
            int rndInt = rnd.nextInt(1000);
            String name = "Name - " + rndInt;
            String uid = "UID - " + rndInt;
            return Context.createSimpleContext(name, uid);
        }
    }

    private static class MyTask implements Runnable {
        public static final ThreadLocal<Context> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();
        public static final ThreadLocal<Optional<String>> THREAD_NAME = new ThreadLocal<>();
        private Context context;

        public MyTask(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            CONTEXT_THREAD_LOCAL.set(context);
            String name = context.name;
            String uid = context.uid;

            String threadName = getThreadNameOrElse(this.getClass().getSimpleName()
                    + " " + Thread.currentThread().getId());

            System.out.println("***************************************************");
            System.out.println(String.format("Thread context 's name/uid : %s/%s", name, uid));
            System.out.println(String.format("Thread name %s", threadName));
            System.out.println("***************************************************");
            System.out.println();

            (new Business()).doBusiness();
        }

        public static String getThreadNameOrElse(String defaultName) {
            Optional<String> optionalThreadName = THREAD_NAME.get();
            if (Objects.isNull(optionalThreadName)) {
                THREAD_NAME.set(Optional.of(defaultName));
            }
            return THREAD_NAME.get().get();
        }
    }

    private static class Business {
        public void doBusiness() {
            System.out.println("Do business job....");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
            Context context = MyTask.CONTEXT_THREAD_LOCAL.get();
            String threadName = MyTask.THREAD_NAME.get().get();
            System.out.println(String.format("Thread context (%s/%s) finished!", context.name, context.uid));
            System.out.println(String.format("Thread name %s finished", threadName));
        }
    }
}
