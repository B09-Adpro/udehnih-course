package id.ac.ui.cs.advprog.udehnihcourse.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${services.payment.api-key}")
    private String validApiKey;

    @Value("${api.header:X-API-Key}")
    private String apiKeyHeader;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("ApiKeyAuthFilter: doFilterInternal called for URI: {}", request.getRequestURI());

        try {
            String apiKey = extractApiKey(request);
            if (StringUtils.hasText(apiKey) && validateApiKey(apiKey)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                null,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_API_CLIENT"))
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("Request authenticated with API key");
            }
        } catch (Exception e) {
            log.error("Cannot set API key authentication: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    private String extractApiKey(HttpServletRequest request) {
        String apiKey = request.getHeader(apiKeyHeader);

        if (StringUtils.hasText(apiKey)) {
            return apiKey;
        }

        return null;
    }

    private boolean validateApiKey(String apiKey) {
        return validApiKey.equals(apiKey);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Define paths that should be filtered by this filter
        String path = request.getRequestURI();
        return !path.startsWith("/api/enrollment/payment-callback"); // Hanya filter dari path ini
    }
}