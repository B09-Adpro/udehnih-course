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
                                .requestMatchers(HttpMethod.PUT,
                                        "/api/internal/tutor-applications/**")
                                .hasRole("STAFF")

                                 .requestMatchers(HttpMethod.POST,
                                         "/api/tutors/apply")
                                .access(allOf(
                                        hasRole("STUDENT"),
                                        not(hasRole("TUTOR"))
                                ))

                                .requestMatchers(HttpMethod.GET,
                                        "/api/tutors/status")
                                .hasAnyRole("STUDENT", "TUTOR")

                                .requestMatchers(HttpMethod.DELETE,
                                        "/api/tutors/apply")
                                .access(allOf(
                                        hasRole("STUDENT"),
                                        not(hasRole("TUTOR"))
                                ))

                                .requestMatchers(HttpMethod.GET,
                                        "/api/tutors/courses")
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

                                .requestMatchers(HttpMethod.POST,
                                        "api/courses/{courseId}/sections")
                                .hasRole("TUTOR")

                                .requestMatchers(HttpMethod.GET,
                                        "api/courses/{courseId}/sections")
                                .authenticated()

                                .requestMatchers(HttpMethod.PUT,
                                        "api/courses/{courseId}/sections/{sectionId}")
                                .hasRole("TUTOR")

                                .requestMatchers(HttpMethod.DELETE,
                                        "api/courses/{courseId}/sections/{sectionId}")
                                .hasRole("TUTOR")

                                .requestMatchers(HttpMethod.POST,
                                        "api/courses/{courseId}/sections/{sectionId}/articles")
                                .hasRole("TUTOR")

                                .requestMatchers(HttpMethod.GET,
                                        "api/courses/{courseId}/sections/{sectionId}/articles")
                                .authenticated()

                                .requestMatchers(HttpMethod.PUT,
                                        "api/courses/{courseId}/sections/{sectionId}/articles/{articlesId}")
                                .hasRole("TUTOR")

                                .requestMatchers(HttpMethod.DELETE,
                                        "api/courses/{courseId}/sections/{sectionId}/articles/{articleId}")
                                .hasRole("TUTOR")

                                .requestMatchers(HttpMethod.GET,
                                        "api/courses/{courseId}/enrollments")
                                .hasRole("TUTOR")

                                .requestMatchers(HttpMethod.POST,
                                        "api/courses/{courseId}/submit-review")
                                .hasRole("TUTOR")


                                .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
