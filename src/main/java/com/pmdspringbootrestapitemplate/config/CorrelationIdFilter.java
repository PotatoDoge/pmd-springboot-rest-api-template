package com.pmdspringbootrestapitemplate.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Get correlation ID from header or generate new one
            String correlationId = request.getHeader(CORRELATION_ID_HEADER);
            if (correlationId == null || correlationId.trim().isEmpty()) {
                correlationId = UUID.randomUUID().toString();
            }

            // Store in MDC for logging
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId);

            // Add to response header
            response.setHeader(CORRELATION_ID_HEADER, correlationId);

            log.debug("Request started - Method: {}, URI: {}, CorrelationId: {}",
                    request.getMethod(), request.getRequestURI(), correlationId);

            filterChain.doFilter(request, response);

            log.debug("Request completed - CorrelationId: {}", correlationId);

        } finally {
            // Clean up MDC to prevent memory leaks
            MDC.remove(CORRELATION_ID_MDC_KEY);
        }
    }
}
