package ru.balancer;

import ru.controllers.TestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class TestMultiThreadManager {
    private final AtomicInteger successCount = new AtomicInteger(0);
    public final AtomicInteger errorCount = new AtomicInteger(0);

    public void start(final int count, final CountDownLatch countDownLatch, final TestController controller) {
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

    public int getSuccess() {
        return successCount.get();
    }

    public int getError() {
        return errorCount.get();
    }
}
