package id.ac.ui.cs.advprog.udehnihcourse.clients;

import id.ac.ui.cs.advprog.udehnihcourse.dto.auth.UserInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class AuthServiceClient {

    private final RestTemplate restTemplate;


    //asumsi kalo auth ga implement service-to-service
    @Value("${services.auth.baseurl}")
    private String authServiceBaseUrl;

    @Autowired
    public AuthServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserInfoResponse getUserInfoById(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            log.warn("getUserInfoById called with null or empty userId.");
            return null;
        }

        String url = authServiceBaseUrl + "/api/users/" + userId;
        log.debug("Calling Auth Service: GET {}", url);

        try {
            ResponseEntity<UserInfoResponse> response = restTemplate.getForEntity(url, UserInfoResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.debug("Successfully fetched user info for userId {}: {}", userId, response.getBody());
                return response.getBody();
            } else {
                log.warn("Failed to fetch user info for userId {}. Status: {}, Body: {}", userId, response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("User not found in Auth Service for userId {}: {}", userId, e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Error calling Auth Service for userId {}: {}", userId, e.getMessage(), e);
            return null;
        }
    }
}
