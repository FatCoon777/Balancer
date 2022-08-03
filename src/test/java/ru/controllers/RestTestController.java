package ru.controllers;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import ru.balancer.CallLimitException;

public class RestTestController implements TestController {
    private final TestRestTemplate restTemplate;

    private final String apiUrl;

    public RestTestController(TestRestTemplate restTemplate, String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
    }

    @Override
    public void execute() throws CallLimitException {
        ResponseEntity<?> responseEntity = restTemplate.getForEntity(apiUrl, null);
        switch (responseEntity.getStatusCodeValue()) {
            case 200:
                break;
            case 502:
                throw new CallLimitException();
            default:
                throw new UnknownStatusException();
        }
    }
}
