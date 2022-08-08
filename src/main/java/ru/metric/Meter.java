package ru.metric;

import ru.balancer.CallLimitException;

public class Meter {
    private final double callTime;

    private volatile double lastTime = 0;

    public Meter(int countPerTime, long timeNanos) {
        callTime = (double) timeNanos / countPerTime;
    }

    public void throttle() throws CallLimitException {
        long currentTime = System.nanoTime();
        if (currentTime > lastTime) {
            synchronized (this) {
                if (currentTime > lastTime) {
                    lastTime = currentTime + callTime;
                } else {
                    throw new CallLimitException();
                }
            }
        } else {
            throw new CallLimitException();
        }
    }
}
