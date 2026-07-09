package com.example.demo.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.Filter;

@Component
public class MdcFilter implements Filter
{
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{

        try{
            // Generate a unique ID for this request
            String requestId = UUID.randomUUID().toString().substring(0, 8);
            // Put it in MDC — now every log on this thread includes it
            MDC.put("requestId", requestId);
            // Also useful: log the HTTP method and URI
            if (request instanceof HttpServletRequest httpRequest) {
                MDC.put("method", httpRequest.getMethod());
                MDC.put("uri", httpRequest.getRequestURI());
            }
            // Proceed with the request
            chain.doFilter(request, response);
        }finally {
            // Always clear MDC — even if exception thrown
            // Without this: next request on this thread inherits old requestId
            MDC.clear();

        }
    }
}
