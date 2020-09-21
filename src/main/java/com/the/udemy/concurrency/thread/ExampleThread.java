package com.the.udemy.concurrency.thread;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Game:
 * hacker 1 guess password by looping 0 -> max_password
 * hacker 2 guess password by looping max_password -> 0
 *
 * police wait for WAIT_TIME to catch all hackers immediately.
 *
 * Let's see who will finish their job first.
 */
public class ExampleThread {

    public static final int MAX_PASSWORD = 6999;
    public static final int WAIT_TIME = 10;

    public static void main(String[] args) {
        Collection<Thread> threads = new ArrayList<>();

        Random random = new Random();
        int rndPass = random.nextInt(MAX_PASSWORD);
        Vault vault = new Vault(rndPass);

        threads.add(new AscendingHacker(vault));
        threads.add(new DescendingHacker(vault));
        threads.add(new Police());

        for (Thread thread : threads) {
            thread.start();
        }
    }

    private static class Vault {
        private int password;
        public Vault(int password) {
            this.password = password;
        }

        public boolean isCorrectPassword(int password) {
            try {
                Thread.sleep(4l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return this.password == (password);
        }
    }

    private abstract static class Hacker extends Thread {
        protected Vault vault;
        public Hacker(Vault vault) {
            this.vault = vault;
            setName(this.getClass().getSimpleName());
            setPriority(Thread.MAX_PRIORITY);
        }

        protected void guessPasswordThenExit(int password) {
            if (!vault.isCorrectPassword(password)) {
                return;
            }

            System.out.println("Vault's password is " + password);
            System.out.println("Guessed successfully by " + this.getName());
            System.exit(0);

        }
    }

    private static class AscendingHacker extends Hacker {
        public AscendingHacker(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for (int i = 0; i < MAX_PASSWORD; i ++) {
                guessPasswordThenExit(i);
            }
        }
    }

    private static class DescendingHacker extends Hacker {
        public DescendingHacker(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for (int i = MAX_PASSWORD; i >= 0; i --) {
                guessPasswordThenExit(i);
            }
        }
    }

    private static class Police extends Thread {
        public Police() {
            this.setName(this.getClass().getSimpleName());
        }

        @Override
        public void run() {
            for (int i = 1; i <= WAIT_TIME; i ++) {
                try {
                    Thread.sleep(1000l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Second: " + i);
            }
            System.out.println("Game over for you hackers");
            System.exit(0);
        }
    }
}
