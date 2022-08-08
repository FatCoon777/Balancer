package ru.balancer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.DemoApplication;
import ru.controller.ApiController;
import ru.controllers.ApiTestController;
import ru.controllers.RestTestController;
import ru.controllers.TestController;

import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = DemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BalancerTest {

    private final TestRestTemplate restTemplate;
    private final ApiController apiController;

    private final String apiUrl;

    private final int countPerTime;
    private final long balancerTime;

    @Autowired
    BalancerTest(TestRestTemplate restTemplate,
                 ApiController apiController,
                 @Value("${api.url}") String apiUrl,
                 @Value("${balancer.countPerTime}") int countPerTime,
                 @Value("${balancer.time.minutes}") int balancerTime) {
        this.restTemplate = restTemplate;
        this.apiController = apiController;
        this.apiUrl = apiUrl;
        this.countPerTime = countPerTime;
        this.balancerTime = TimeUnit.NANOSECONDS.convert(balancerTime, TimeUnit.MINUTES);
    }

    @Test
    void testStatuses() throws InterruptedException {
        final int requestCount = 20;
        TestController controller = new RestTestController(restTemplate, apiUrl);
        TestMultiThreadManager manager = new TestMultiThreadManager(requestCount, controller);

        long time = System.nanoTime();
        manager.start();
        manager.await();
        time = System.nanoTime() - time;

        double callTime = (double) balancerTime / countPerTime;
        double intervalCount = Math.floor(time / callTime);
        final double successCount = intervalCount == 0 ? 1 : intervalCount;
        final double errorCount = requestCount - successCount;

        Assertions.assertAll(
                () -> Assertions.assertEquals(manager.getSuccess(), successCount),
                () -> Assertions.assertEquals(manager.getError(), errorCount)
        );
    }

    @Test
    void testDifferentIp() throws InterruptedException {
        final int requestCount = 20;

        TestController controller1 = new ApiTestController("111.111.111.111", apiController);
        TestController controller2 = new ApiTestController("222.222.222.222", apiController);

        TestMultiThreadManager manager1 = new TestMultiThreadManager(requestCount, controller1);
        TestMultiThreadManager manager2 = new TestMultiThreadManager(requestCount, controller2);

        long time = System.nanoTime();
        manager1.start();
        manager2.start();

        manager1.await();
        manager2.await();
        time = System.nanoTime() - time;

        double callTime = (double) balancerTime / countPerTime;
        double intervalCount = Math.floor(time / callTime);
        final double successCount = intervalCount == 0 ? 1 : intervalCount;
        final double errorCount = requestCount - successCount;

        Assertions.assertAll(
                () -> Assertions.assertEquals(manager1.getSuccess(), successCount),
                () -> Assertions.assertEquals(manager1.getError(), errorCount),
                () -> Assertions.assertEquals(manager2.getSuccess(), successCount),
                () -> Assertions.assertEquals(manager2.getError(), errorCount)
        );
    }
}
