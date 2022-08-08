package ru.metric;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.balancer.CallLimitException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Service
public class MetricManager {
    private final int countPerTime;
    private final long time;

    private final ConcurrentMap<String, Meter> meterMap = new ConcurrentHashMap<>();

    public MetricManager(@Value("${balancer.countPerTime}") int countPerTime,
                         @Value("${balancer.time.minutes}") int time) {
        this.countPerTime = countPerTime;
        this.time = TimeUnit.NANOSECONDS.convert(time, TimeUnit.MINUTES);
    }

    public void check(String name) throws CallLimitException {
        Meter meter = meterMap.computeIfAbsent(name, key -> new Meter(countPerTime, time));
        meter.throttle();
    }
}
