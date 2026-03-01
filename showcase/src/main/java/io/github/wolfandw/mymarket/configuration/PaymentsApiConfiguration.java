package io.github.wolfandw.mymarket.configuration;

import io.github.wolfandw.payment.client.ApiClient;
import io.github.wolfandw.payment.client.api.PaymentsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация подключения Api сервиса платежей.
 */
@Configuration
public class PaymentsApiConfiguration {
    @Value("${paymentsService.url}")
    private String paymentsServiceUrl;

    /**
     * Создает Api клиента сервиса платежей.
     *
     * @return Api клиента сервиса платежей
     */
    @Bean
    public ApiClient paymentsApiClient() {
        return new ApiClient().setBasePath(paymentsServiceUrl);
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

