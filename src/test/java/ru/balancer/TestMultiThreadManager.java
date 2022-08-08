package ru.balancer;

import ru.controllers.TestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class TestMultiThreadManager {
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger errorCount = new AtomicInteger(0);

    private final int count;
    private final CountDownLatch countDownLatch;
    private final TestController controller;

    public TestMultiThreadManager(int count, TestController controller) {
        this.count = count;
        this.countDownLatch = new CountDownLatch(count);
        this.controller = controller;
    }

    public void start() {
        for (int i = 0; i < count; i++) {
            new Thread(() -> {
                try {
                    controller.execute();
                    successCount.incrementAndGet();

                } catch (CallLimitException e) {
                    errorCount.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            }).start();
        }
    }

    public void await() throws InterruptedException {
        countDownLatch.await();
    }

    public int getSuccess() {
        return successCount.get();
    }

    public int getError() {
        return errorCount.get();
    }
}
