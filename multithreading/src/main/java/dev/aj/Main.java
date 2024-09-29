package dev.aj;

import lombok.SneakyThrows;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {
    @SneakyThrows
    public static void main(String[] args) {

        ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        rwLock.writeLock().lock();
        System.out.printf("Main thread [%s].%n", Thread.currentThread().getName());
        rwLock.writeLock().unlock();
        Thread thread = new Thread(() -> {
       /*     try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/

            System.out.printf("Thread [%s].%n", Thread.currentThread().getName());

//            throw new RuntimeException("I made it do it");
        });

        thread.setUncaughtExceptionHandler((throwingThread, throwable) -> {
            System.out.printf("uncaught exception thrown in [%s], [%s].%n", throwingThread.getName(), throwable.getMessage());
        });

//        thread.setDaemon(true);
        thread.setName("worker");
//        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
//        Thread.sleep(500);

        MyThread myThread = new MyThread();
        myThread.setDaemon(true);
        myThread.setName("pigeon");
        myThread.start();

        thread.join();
        myThread.join();

        System.out.printf("Main thread [%s], other thread [%s] %n", Thread.currentThread().getName(), thread.getName());
    }

    private static class MyThread extends Thread {

        @Override
        public void run() {
            System.out.printf("Thread of [%s]%n", this.getName());
        }
    }
}