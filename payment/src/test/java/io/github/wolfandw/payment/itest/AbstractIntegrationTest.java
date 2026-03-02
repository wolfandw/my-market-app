package io.github.wolfandw.payment.itest;

import io.github.wolfandw.payment.service.PaymentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Абстрактный интеграционный тест.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebTestClient
public abstract class AbstractIntegrationTest {
    /**
     * Сервис платежей.
     */
    @Autowired
    protected PaymentsService paymentsService;

    /**
     * Тестовый реактивный клиент.
     */
    @Autowired
    protected WebTestClient webTestClient;

}
