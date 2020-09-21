package com.the.udemy.exercise.first;

import java.util.List;
import java.util.function.Consumer;

/**
 * Create a pub-sub (1 vs 1) lib using wait and notify
 */

public class Main {
    public static void main(String args[]) {
        Channel channel = new Channel();
        SubscriberChannel subscriberChannel = channel;
        PublisherChannel publisherChannel = channel;

        List<String> messages = List.of("Hello", "This", "Is", "The", "Phan", "end");
        Thread pubThread = new Thread(new Publisher(publisherChannel, messages), "pub thread");
        Thread subThread = new Thread(new Subscriber(subscriberChannel, messages), "sub thread");

        subThread.start();
        pubThread.start();
    }

}

class Publisher implements Runnable {
    private final PublisherChannel channel;
    private final List<String> messages;

    public Publisher(PublisherChannel channel, List<String> messages) {
        this.channel = channel;
        this.messages = messages;
    }

    @Override
    public void run() {
        Consumer<String> business =  message -> {
            System.out.println("PUBLISHER -- ");
            System.out.println("Published message: " + message);
            System.out.println();
        };
        for (String msg : messages) {
            channel.publish(msg, business);
        }
    }
}

class Subscriber implements Runnable {
    private final SubscriberChannel channel;
    private final List<String> messages;

    public Subscriber(SubscriberChannel channel, List<String> messages) {
        this.channel = channel;
        this.messages = messages;
    }

    @Override
    public void run() {
        Consumer<String> subscribeBusiness = message -> {
            System.out.println("SUBSCRIBER -- ");
            System.out.println("Subscribed message: " + message);
            System.out.println();
        };
        for (String msg : messages) {
            channel.subscribe(msg, subscribeBusiness);
        }
        Thread.currentThread().interrupt();
    }
}

interface SubscriberChannel {
    void subscribe(String message, Consumer<String> subscribeBusiness);
}

interface PublisherChannel {
    void publish(String message, Consumer<String> publishBusiness);
}

class Channel implements SubscriberChannel, PublisherChannel {
    public static final long WAITING_TIME = 500L;
    private boolean isSent;

    public synchronized void publish(String message, Consumer<String> publishBusiness) {
        try {
            while (isSent) {
                this.wait();
            }
            Thread.sleep(300l);
            publishBusiness.accept(message);
            isSent = true;
            this.notifyAll();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }

    }

    public synchronized void subscribe(String message, Consumer<String> subscribeBusiness) {
        try {
            while (!isSent) {
                this.wait(1000l);
            }
            Thread.sleep(WAITING_TIME);
            subscribeBusiness.accept(message);
            isSent = false;
            this.notifyAll();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
