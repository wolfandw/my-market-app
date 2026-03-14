package io.github.wolfandw.mymarket.controller.advicer;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

/**
 * Обработчик исключений контроллеров.
 */
@ControllerAdvice
public class ApplicationControllerAdvicer {
    private static final String TEMPLATE_ERROR = "error";
    private static final String ATTRIBUTE_ERROR = "error";
    private static final String ATTRIBUTE_STATUS = "status";

    /**
     * Обрабатывает исключение MethodArgumentTypeMismatchException.
     *
     * @param e исключение типа IllegalArgumentException
     * @return имя шаблона ошибки
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<Rendering> handleBadRequest(IllegalArgumentException e) {
        return Mono.just(
                Rendering.view(TEMPLATE_ERROR)
                        .modelAttribute(ATTRIBUTE_ERROR, e.getMessage())
                        .modelAttribute(ATTRIBUTE_STATUS, HttpServletResponse.SC_BAD_REQUEST)
                        .build()
        );
    }

    /**
     * Обрабатывает исключение AuthorizationDeniedException.
     *
     * @param e исключение типа AuthorizationDeniedException
     * @return имя шаблона ошибки
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public Mono<Rendering> handleAuthorizationDeniedException(DataAccessException e) {
        return Mono.just(Rendering.redirectTo("/login").build());
    }

    /**
     * Обрабатывает непредвиденные исключения.
     *
     * @param e исключение типа Exception
     * @return имя шаблона ошибки
     */
    @ExceptionHandler(RuntimeException.class)
    public Mono<Rendering> handleGenericException(RuntimeException e) {
        return Mono.just(
                Rendering.view(TEMPLATE_ERROR)
                        .modelAttribute(ATTRIBUTE_ERROR, e.getMessage())
                        .modelAttribute(ATTRIBUTE_STATUS, HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                        .build()
        );
    }

    /**
     * Обрабатывает непредвиденные исключения.
     *
     * @param e исключение типа Exception
     * @return имя шаблона ошибки
     */
    @ExceptionHandler(Exception.class)
    public Mono<Rendering> handleGenericException(Exception e) {
        return Mono.just(
                Rendering.view(TEMPLATE_ERROR)
                        .modelAttribute(ATTRIBUTE_ERROR, e.getMessage())
                        .modelAttribute(ATTRIBUTE_STATUS, HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                        .build()
        );
    }
}