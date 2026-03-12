package io.github.wolfandw.mymarket.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler;

import java.net.URI;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Конфигурация безопасности.
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {
    private static final String GUEST_USER = "guest";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public RedirectServerLogoutSuccessHandler redirectServerLogoutSuccessHandler() {
        RedirectServerLogoutSuccessHandler logoutSuccessHandler = new RedirectServerLogoutSuccessHandler();
        logoutSuccessHandler.setLogoutSuccessUrl(URI.create("/"));
        return logoutSuccessHandler;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        ServerCsrfTokenRequestAttributeHandler csrfTokenRequestHandler = new ServerCsrfTokenRequestAttributeHandler();
        csrfTokenRequestHandler.setTokenFromMultipartDataEnabled(true);
        return http
                .csrf(csrf -> csrf
                    .csrfTokenRepository(new CookieServerCsrfTokenRepository())
                    .csrfTokenRequestHandler(csrfTokenRequestHandler)
                )
                .authorizeExchange(exchanges -> exchanges
                    .pathMatchers("/", "/login", "/static/**").permitAll()

                    .pathMatchers(HttpMethod.GET, "/items*").permitAll()
                    .pathMatchers(HttpMethod.POST, "/items*").authenticated()

                    .pathMatchers(HttpMethod.GET, "/items/*").permitAll()
                    .pathMatchers(HttpMethod.POST, "/items/*").authenticated()

                    .pathMatchers(HttpMethod.GET,"/items/new").authenticated()
                    .pathMatchers(HttpMethod.POST,"/items/new").authenticated()

                    .pathMatchers(HttpMethod.GET,"/items/*/image").permitAll()
                    .pathMatchers(HttpMethod.POST,"/items/*/image").authenticated()

                    .pathMatchers(HttpMethod.GET,"/cart/items").authenticated()
                    .pathMatchers(HttpMethod.POST,"/cart/items*").authenticated()

                    .pathMatchers(HttpMethod.GET,"/orders").authenticated()
                    .pathMatchers(HttpMethod.POST,"/orders/*").authenticated()

                    .pathMatchers(HttpMethod.POST,"/buy").authenticated()
                    .pathMatchers(HttpMethod.POST,"/topUpBalance").authenticated()

                    .anyExchange().authenticated()
                )
                .formLogin(form -> form
                    .authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler("/"))
                )
                .anonymous(anonymous -> anonymous
                        .principal(GUEST_USER)
                )
                .logout(logout -> logout
                    .logoutHandler(new WebSessionServerLogoutHandler())
                        .logoutSuccessHandler(redirectServerLogoutSuccessHandler())
                )
                .oauth2Client(withDefaults())
                .build();
    }
}
