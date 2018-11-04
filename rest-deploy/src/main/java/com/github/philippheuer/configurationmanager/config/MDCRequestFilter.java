package com.github.philippheuer.configurationmanager.config;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;
import java.util.UUID;

/**
 * Mapped Diagnostic Context Injector
 *
 * This class adds a unique request id into the logging context.
 */
@Component
public class MDCRequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            // Setup MDC data:
            MDC.put("requestId", UUID.randomUUID().toString());
            chain.doFilter(request, response);
        } finally {
            // Tear down MDC data: ( Important! Cleans up the ThreadLocal data again )
            MDC.clear();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

}
