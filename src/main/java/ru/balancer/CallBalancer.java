package ru.balancer;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import ru.Context;
import ru.metric.MetricService;

@Aspect
@Component
public class CallBalancer {
    private final MetricService metricService;

    public CallBalancer(MetricService metricService) {
        this.metricService = metricService;
    }

    @Before("@annotation(CallControl)")
    public void balance(JoinPoint joinPoint) throws CallLimitException {
        String meterName = getMeterName(joinPoint.getSignature());
        metricService.check(meterName);
    }

    private String getMeterName(Signature signature) {
        Class<?> type = signature.getDeclaringType();
        String methodName = signature.getName();

        String meterName = null;

        try {
            CallControl callControl = type.getMethod(methodName).getAnnotation(CallControl.class);
            meterName = callControl.meterName();
        } catch (Exception ignore) {
        }

        if (meterName == null || meterName.isEmpty()) {
            meterName = signature.getDeclaringTypeName() + "." + methodName;
        }

        return Context.getIp() + ":" + meterName;
    }
}
