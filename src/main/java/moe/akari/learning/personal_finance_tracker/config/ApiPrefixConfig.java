package moe.akari.learning.personal_finance_tracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration to add /api prefix to all REST controllers
 */
@Configuration
public class ApiPrefixConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // Add /api prefix to all @RestController annotated classes
        configurer.addPathPrefix("/api", c ->
            c.isAnnotationPresent(org.springframework.web.bind.annotation.RestController.class));
    }
}

