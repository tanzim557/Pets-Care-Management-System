package com.petscare.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@Order(1)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        // Log incoming request
        logRequest(requestWrapper);

        // Continue with the request
        filterChain.doFilter(requestWrapper, responseWrapper);

        long duration = System.currentTimeMillis() - startTime;

        // Log response
        logResponse(requestWrapper, responseWrapper, duration);

        // IMPORTANT: Copy the cached response content to the actual response
        responseWrapper.copyBodyToResponse();
    }

    private void logRequest(HttpServletRequest request) {
        String queryString = request.getQueryString();
        String uri = request.getRequestURI() + (queryString != null ? "?" + queryString : "");

        logger.info("╔═══════════════════════════════════════════════════════════════════════════");
        logger.info("║ 🌐 REQUEST  │ {} {}", request.getMethod(), uri);
        logger.info("║ 📍 Client   │ {}", request.getRemoteAddr());

        // Log request parameters if present
        if (!request.getParameterMap().isEmpty()) {
            String params = request.getParameterMap().entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
                    .collect(Collectors.joining(", "));
            logger.info("║ 📝 Params   │ {}", params);
        }
    }

    private void logResponse(HttpServletRequest request, ContentCachingResponseWrapper response, long duration) {
        int status = response.getStatus();
        String statusIcon = status >= 200 && status < 300 ? "✅" : status >= 400 ? "❌" : "⚠️";

        logger.info("║─────────────────────────────────────────────────────────────────────────");
        logger.info("║ {} RESPONSE │ Status: {} │ Duration: {}ms", statusIcon, status, duration);

        // Log response body if present and not too large
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0 && content.length < 2000) {
            try {
                String responseBody = new String(content, response.getCharacterEncoding());
                // Pretty print JSON if it's JSON
                if (responseBody.startsWith("{") || responseBody.startsWith("[")) {
                    logger.info("║ 📤 Response │ {}", responseBody);
                } else {
                    logger.info("║ 📤 Response │ {}", responseBody);
                }
            } catch (UnsupportedEncodingException e) {
                logger.info("║ 📤 Response │ <Could not decode>");
            }
        } else if (content.length >= 2000) {
            logger.info("║ 📤 Response │ <{} bytes>", content.length);
        }

        logger.info("╚═══════════════════════════════════════════════════════════════════════════");
        logger.info(""); // Empty line for readability
    }
}
