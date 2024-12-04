package fr.mcoolive.echo_bot;

import fr.mcoolive.echo_bot.service.RulesEngine;
import fr.mcoolive.echo_bot.service.RulesEngineFactory;
import fr.mcoolive.echo_bot.service.RulesLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;
import java.util.List;

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
    RulesEngine<Object, String> getRulesEngine(RulesEngineFactory factory) {
        final List<String> cfgPaths = Arrays.asList("./src/test/resources/dynamic-response.csv", "./src/test/resources/dynamic-response.yaml");
        final String fallbackOutput = "default";
        return factory.newRulesEngine(cfgPaths, fallbackOutput);
    }
}
