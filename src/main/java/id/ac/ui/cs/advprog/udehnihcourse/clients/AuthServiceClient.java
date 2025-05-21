package id.ac.ui.cs.advprog.udehnihcourse.clients;

import id.ac.ui.cs.advprog.udehnihcourse.config.FeignConfig;
import id.ac.ui.cs.advprog.udehnihcourse.dto.auth.UserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "auth-service",
        url = "${services.auth.baseurl}",
        configuration = FeignConfig.class
)

public interface AuthServiceClient {
    @GetMapping("/api/users/{userId}")
    UserInfoResponse getUserInfoById(@PathVariable("userId") String userId);
}