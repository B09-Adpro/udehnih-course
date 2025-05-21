package id.ac.ui.cs.advprog.udehnihcourse.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignRequestInterceptor implements RequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(FeignRequestInterceptor.class);

    @Override
    public void apply(RequestTemplate template) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getCredentials() instanceof String) {
                String token = (String) authentication.getCredentials();
                template.header("Authorization", "Bearer " + token);
                logger.info("Forwarding token from authentication credentials");
            } else {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    String authHeader = request.getHeader("Authorization");
                    if (authHeader != null && !authHeader.isEmpty()) {
                        template.header("Authorization", authHeader);
                        logger.info("Forwarding token from request header");
                    } else {
                        logger.warn("No authentication token found to forward!");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error in FeignRequestInterceptor: {}", e.getMessage(), e);
        }
    }
}
