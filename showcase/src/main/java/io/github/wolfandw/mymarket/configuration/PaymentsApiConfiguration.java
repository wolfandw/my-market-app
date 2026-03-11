package io.github.wolfandw.mymarket.configuration;

import io.github.wolfandw.payment.client.ApiClient;
import io.github.wolfandw.payment.client.api.PaymentsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Конфигурация подключения Api сервиса платежей.
 */
@Configuration
public class PaymentsApiConfiguration {
    @Value("${paymentsService.url}")
    private String paymentsServiceUrl;

    @Bean
    public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
            ReactiveClientRegistrationRepository clientRegistrations,
            ServerOAuth2AuthorizedClientRepository authorizedClients) {
        ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .refreshToken()
                .build();
        DefaultReactiveOAuth2AuthorizedClientManager authorizedClientManager = new DefaultReactiveOAuth2AuthorizedClientManager(
                clientRegistrations, authorizedClients);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authorizedClientManager;
    }

    /**
     * Создает Api клиента сервиса платежей.
     *
     * @return Api клиента сервиса платежей
     */
    @Bean
    public ApiClient paymentsApiClient(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Client = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2Client.setDefaultOAuth2AuthorizedClient(true);
        oauth2Client.setDefaultClientRegistrationId("keycloak");
        WebClient webClient = WebClient.builder()
                .baseUrl(paymentsServiceUrl)
                .filter(oauth2Client)
                .build();
        return new ApiClient(webClient);
    }

    /**
     * Создает Api сервиса платежей.
     *
     * @param paymentsApiClient  Api клиента сервиса платежей
     * @return Api сервиса платежей
     */
    @Bean
    public PaymentsApi paymentsApi(ApiClient paymentsApiClient) {
        return new PaymentsApi(paymentsApiClient);
    }
}

