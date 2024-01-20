package com.example.demo;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@AutoConfiguration
public class LoggingFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    log.info("Request: {} :: {}", req.getMethod(), req.getRequestURI());
    if (log.isDebugEnabled()) {
      req.getHeaderNames().asIterator().forEachRemaining(name -> {
        log.debug("Header: {} :: {}", name, req.getHeader(name));
      });
    }
    chain.doFilter(request, response);
  }

}
