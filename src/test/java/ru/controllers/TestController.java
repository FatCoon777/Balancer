package ru.controllers;

import ru.balancer.CallLimitException;

public interface TestController {
    void execute() throws CallLimitException;
}
