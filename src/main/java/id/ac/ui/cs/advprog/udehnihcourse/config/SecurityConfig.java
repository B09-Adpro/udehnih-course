package id.ac.ui.cs.advprog.udehnihcourse.config;

import id.ac.ui.cs.advprog.udehnihcourse.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;
import static org.springframework.security.authorization.AuthorizationManagers.not;
import static org.springframework.security.authorization.AuthorizationManagers.allOf;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                 .requestMatchers(HttpMethod.POST,
                                         "/api/tutors/apply")// Hanya Student yang bisa apply
                                .access(allOf(
                                        hasRole("STUDENT"),
                                        not(hasRole("TUTOR"))
                                ))

                                .requestMatchers(HttpMethod.GET,
                                        "/api/tutors/status")
                                .hasAnyRole("STUDENT", "TUTOR") // Student dan Tutor yang bisa cek status dia diterima atau ngga

                                .requestMatchers(HttpMethod.DELETE,
                                        "/api/tutors/apply")
                                .access(allOf(
                                        hasRole("STUDENT"),
                                        not(hasRole("TUTOR"))
                                )) // Hanya Student yang bisa menggagalkan applicancenya sendiri

                                .requestMatchers(HttpMethod.GET,
                                        "/api/tutors/courses")
                                .hasRole("TUTOR") // Hanya Tutor yang bisa cek courses-nya sendiri

                                .requestMatchers(HttpMethod.GET,
                                        "/api/courses")
                                .hasRole("TUTOR")

                                .requestMatchers(HttpMethod.PUT,
                                        "/api/courses/{courseId}")
                                .hasRole("TUTOR")

                                .requestMatchers(HttpMethod.DELETE,
                                        "/api/courses/{courseId}")
                                .hasRole("TUTOR")

                                .requestMatchers(HttpMethod.GET,
                                        "/api/courses/{courseId}/enrollments")
                                .hasRole("TUTOR")

                                .anyRequest().authenticated()
                );
//                .authorizeHttpRequests(authorizeRequests ->
//                                authorizeRequests.anyRequest().permitAll()
//                );
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
