package fr.mcoolive.echo_bot;

import fr.mcoolive.echo_bot.service.RulesEngine;
import fr.mcoolive.echo_bot.service.RulesLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableAsync
public class WebhookConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable() // Disable CSRF protection
                .build();
    }

    @Bean
    RulesLoader getRulesLoader() {
        return new RulesLoader();
    }

    @Bean
    RulesEngine<Map<String, Object>, Map<String, Object>> getRulesEngine(RulesLoader rulesLoader) {
        final List<String> cfgPaths = Arrays.asList("./src/test/resources/dynamic-response.csv", "./src/test/resources/dynamic-response.yaml");
        final Map<String, Object> fallbackMap = new HashMap<String, Object>() {{
            put("fall", "back");
        }};
        final RulesEngine<Map<String, Object>, Map<String, Object>> fallback = RulesEngine.fallback(fallbackMap);
        return cfgPaths.stream()
                .map(p -> rulesLoader.loadRules(p, 0L)).map(RulesEngine::basic)
                .reduce(RulesEngine::andThen)
                .map(e -> e.andThen(fallback)).orElse(fallback);
    }
}
