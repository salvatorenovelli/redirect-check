package com.snovelli;

import org.apache.http.annotation.ThreadSafe;

import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
public class ProgressMonitor implements Runnable {

    public static final int COMPLETION_BAR_WIDTH = 30;

    private final int totalTicksRequired;
    private final Thread thread = new Thread(this, ProgressMonitor.class.getName());
    private final AtomicInteger ticks = new AtomicInteger(0);

    public ProgressMonitor(int totalTicksRequired) {
        this.totalTicksRequired = totalTicksRequired;
    }

    public void startPrinting() {
        thread.start();
    }

    public int tick() {
        return ticks.incrementAndGet();
    }

    private synchronized void printCompletionPercentage() {
        System.out.print("|");
        int i = 0;
        for (; i < getPercentage() / 100.0 * COMPLETION_BAR_WIDTH; i++) {
            System.out.print("=");
        }
        for (; i < COMPLETION_BAR_WIDTH; i++) {
            System.out.print("-");
        }
        System.out.printf("| %d%%\r" + (getPercentage() == 100 ? "\n" : ""), (int) getPercentage());
    }


    private double getPercentage() {
        return ticks.get() / (double) totalTicksRequired * 100.0;
    }

    @Override
    public void run() {
        while (getPercentage() < 100) {
            try {
                printCompletionPercentage();
                Thread.sleep(200);
            } catch (InterruptedException ignore) {

            }
        }

        printCompletionPercentage();
    }
}
