package ru.metric;

import ru.balancer.CallLimitException;

import java.util.LinkedList;
import java.util.Queue;

public class Meter {
    private final int countPerTime;
    private final long time;

    private final Queue<Long> queue = new LinkedList<>();

    public Meter(int countPerTime, long time) {
        this.countPerTime = countPerTime;
        this.time = time;
    }

    public synchronized void increase() throws CallLimitException {
        long currentTime = System.nanoTime();
        long thresholdTime = currentTime - time;
        boolean isComplete = true;
        while (isComplete) {
            Long beforeTime = queue.peek();
            if (beforeTime != null && beforeTime < thresholdTime) {
                queue.poll();
            } else {
                isComplete = false;
            }
        }
        queue.offer(currentTime);
        if (queue.size() > countPerTime) {
            throw new CallLimitException();
        }
    }
}
