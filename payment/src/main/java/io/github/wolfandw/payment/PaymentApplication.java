package io.github.wolfandw.payment;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Spring Boot Application.
 */
@SpringBootApplication
public class PaymentApplication {
    /**
     * Точка входа.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }

    /**
     * WebMvcConfigurer с настроенным CORS.
     *
     * @return WebMvcConfigurer с настроенным CORS
     */
    @Bean
    public WebFluxConfigurer corsConfigurer() {
        return new WebFluxConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOriginPatterns("http://localhost")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}
