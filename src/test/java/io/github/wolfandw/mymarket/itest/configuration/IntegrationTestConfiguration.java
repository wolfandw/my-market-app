package io.github.wolfandw.mymarket.itest.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.ReactiveTransactionManager;
import reactor.test.StepVerifier;

@Configuration
public class IntegrationTestConfiguration {
    /**
     * Создает транзакционный враппер для {@link StepVerifier}.
     *
     * @param reactiveTransactionManager менеджер транзакций
     * @return транзакционный враппер для {@link StepVerifier}
     */
    @Bean
    public TrxStepVerifier trxStepVerifier(ReactiveTransactionManager reactiveTransactionManager) {
        return new TrxStepVerifier(reactiveTransactionManager);
    }
}
