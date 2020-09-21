package com.the.udemy.exercise.first;

import java.util.List;

/**
 * Create a pub-sub (1 vs 1) lib using wait and notify
 */

public class Main {
    public static void main(String args[]) {
        Channel channel = new Channel();
        SubscriberChannel subscriberChannel = channel;
        PublisherChannel publisherChannel = channel;

        List<String> messages = List.of("Hello", "This", "Is", "The", "Phan");
        Thread pubThread = new Thread(new Publisher(publisherChannel, messages));
        Thread subThread = new Thread(new Subscriber(subscriberChannel, messages));

        pubThread.start();
        subThread.start();
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
        for (String msg : messages) {
            channel.publish(msg);
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
        for (String msg : messages) {
            channel.subscribe(msg);
        }
    }
}

interface SubscriberChannel {
    void subscribe(String message);
}

interface PublisherChannel {
    void publish(String message);
}

class Channel implements SubscriberChannel, PublisherChannel {
    public static final long WAITING_TIME = 500L;
    private boolean isSent;

    public synchronized void publish(String message) {

        try {
            while (isSent) {
                this.wait();
            }
            Thread.sleep(300l);
            System.out.println("PUBLISHER -- ");
            System.out.println("Published message: " + message);
            System.out.println();
            isSent = true;
            this.notify();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }

    }

    public synchronized void subscribe(String message) {
        try {
            while (!isSent) {
                this.wait();
            }

            Thread.sleep(WAITING_TIME);
            System.out.println("SUBSCRIBER -- ");
            System.out.println("Subscribed message: " + message);
            System.out.println();
            isSent = false;
            this.notify();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
