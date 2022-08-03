package ru.controller;

import ru.Context;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter
public class ApiFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Context.setIp(request.getRemoteAddr());
        chain.doFilter(request, response);
    }
}
