package id.ac.ui.cs.advprog.udehnihcourse.clients;

import id.ac.ui.cs.advprog.udehnihcourse.config.FeignConfig;
import id.ac.ui.cs.advprog.udehnihcourse.dto.auth.RoleRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.auth.RoleResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.auth.UserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.concurrent.CompletableFuture;

@FeignClient(
        name = "auth-service",
        url = "${services.auth.baseurl}",
        configuration = FeignConfig.class
)

public interface AuthServiceClient {
    @GetMapping("/api/users/{userId}")
    UserInfoResponse getUserInfoById(@PathVariable("userId") String userId);

    @GetMapping("/api/users/{userId}")
    CompletableFuture<UserInfoResponse> getUserInfoByIdAsync(@PathVariable("userId") String userId);

    @PostMapping("/api/roles/add")
    RoleResponse addRoleToUser(@RequestBody RoleRequest roleRequest);
}