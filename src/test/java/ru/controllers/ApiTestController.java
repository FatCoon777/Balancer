package ru.controllers;

import ru.Context;
import ru.balancer.CallLimitException;
import ru.controller.ApiController;

public class ApiTestController implements TestController {
    private final String ip;
    private final ApiController apiController;

    public ApiTestController(String ip, ApiController apiController) {
        this.ip = ip;
        this.apiController = apiController;
    }

    @Override
    public void execute() throws CallLimitException {
        Context.setIp(ip);
        apiController.someMethod();
    }
}
