package fr.mcoolive.crud_png;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebhookConfiguration {

    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable) // FIXME: CSRF should be enabled
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(it -> it.anyRequest().authenticated())
                .build();
    }

    @Bean
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher(EndpointRequest.toAnyEndpoint())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(it -> it
                        .requestMatchers(EndpointRequest.to("health")).permitAll()  // Allow public access to health check
                        .requestMatchers(EndpointRequest.to("prometheus")).authenticated()  // Require authentication for Prometheus metrics
                        .anyRequest().authenticated()  // Secure all other endpoints
                )
                .build();
    }
}
