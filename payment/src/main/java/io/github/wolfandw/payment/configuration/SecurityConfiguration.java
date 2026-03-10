package io.github.wolfandw.payment.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * Конфигурация безопасности.
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityConfiguration.class);

    /**
     * Настраивает цепочку фильтров безопасности.
     *
     * @param http настройки безопасности
     * @return цепочка фильтров безопасности
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/api/payments/**").hasRole("PAYMENTS_SERVICE_CLIENT")
                            .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 ->
                    oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
        return http.build();
    }

    /**
     * Возвращает конвертер токенов.
     *
     * @return конвертер токенов.
     */
    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();

        Converter<Jwt, Flux<GrantedAuthority>> jwtGrantedAuthoritiesConverter = jwt ->
                Mono.justOrEmpty(jwt.getClaimAsMap("realm_access"))
                .flatMapMany(realmAccess -> {
                    Object roles = realmAccess.get("roles");
                    if (roles instanceof Collection<?> rolesList) {
                        return Flux.fromIterable(rolesList)
                                .filter(String.class::isInstance)
                                .map(role -> {
                                    LOG.info("******* Payments Service Role Accepted {}", role);
                                    return new SimpleGrantedAuthority("ROLE_" + role);
                                });
                    }
                    return Flux.empty();
                });
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return converter;
    }
}
