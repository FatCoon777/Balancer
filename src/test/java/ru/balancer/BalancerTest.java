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

import java.util.concurrent.CountDownLatch;

@SpringBootTest(classes = DemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BalancerTest {

    private final TestRestTemplate restTemplate;
    private final ApiController apiController;

    private final String apiUrl;

    private final int countPerTime;

    @Autowired
    BalancerTest(TestRestTemplate restTemplate,
                 ApiController apiController,
                 @Value("${api.url}") String apiUrl,
                 @Value("${balancer.countPerTime}") int countPerTime) {
        this.restTemplate = restTemplate;
        this.apiController = apiController;
        this.apiUrl = apiUrl;
        this.countPerTime = countPerTime;
    }

    @Test
    void testStatuses() throws InterruptedException {

        final int count = countPerTime + 15;
        final CountDownLatch countDownLatch = new CountDownLatch(count);

        TestController controller = new RestTestController(restTemplate, apiUrl);
        TestMultiThreadManager manager = new TestMultiThreadManager();

        manager.start(count, countDownLatch, controller);
        countDownLatch.await();

        Assertions.assertAll(
                () -> Assertions.assertEquals(manager.getSuccess(), countPerTime),
                () -> Assertions.assertEquals(manager.getError(), count - countPerTime)
        );
    }

    @Test
    void testDifferentIp() throws InterruptedException {
        int count = countPerTime + 15;
        CountDownLatch countDownLatch = new CountDownLatch(count * 2);

        TestController controller1 = new ApiTestController("111.111.111.111", apiController);
        TestController controller2 = new ApiTestController("222.222.222.222", apiController);

        TestMultiThreadManager manager1 = new TestMultiThreadManager();
        TestMultiThreadManager manager2 = new TestMultiThreadManager();
        manager1.start(count, countDownLatch, controller1);
        manager2.start(count, countDownLatch, controller2);

        countDownLatch.await();
        Assertions.assertAll(
                () -> Assertions.assertEquals(manager1.getSuccess(), countPerTime),
                () -> Assertions.assertEquals(manager1.getError(), count - countPerTime),
                () -> Assertions.assertEquals(manager2.getSuccess(), countPerTime),
                () -> Assertions.assertEquals(manager2.getError(), count - countPerTime)
        );
    }
}
