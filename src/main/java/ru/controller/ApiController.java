package ru.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.balancer.CallControl;
import ru.balancer.CallLimitException;

import javax.servlet.http.HttpServletResponse;

@RestController
public class ApiController {
    @CallControl
    @GetMapping("${api.url}")
    public void someMethod() throws CallLimitException {
    }

    @ExceptionHandler(CallLimitException.class)
    public void handleException(HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_GATEWAY.value());
    }
}
