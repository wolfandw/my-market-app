package io.github.wolfandw.mymarket.controller.advicer;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Обработчик исключений контроллеров.
 */
//@ControllerAdvice
public class ApplicationControllerAdvicer {
    private static final String TEMPLATE_ERROR = "error";
    private static final String ATTRIBUTE_ERROR = "error";
    private static final String ATTRIBUTE_STATUS = "status";

    /**
     * Обрабатывает исключение MethodArgumentTypeMismatchException.
     *
     * @param e        исключение типа IllegalArgumentException
     * @param model    модель
     * @param response ответ
     * @return имя шаблона ошибки
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleBadRequest(IllegalArgumentException e, Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        model.addAttribute(ATTRIBUTE_ERROR, e.getMessage());
        model.addAttribute(ATTRIBUTE_STATUS, HttpServletResponse.SC_BAD_REQUEST);
        return TEMPLATE_ERROR;
    }

    /**
     * Обрабатывает исключение DataAccessException.
     *
     * @param e        исключение типа DataAccessException
     * @param model    модель
     * @param response ответ
     * @return имя шаблона ошибки
     */
    @ExceptionHandler(DataAccessException.class)
    public String handleDatabaseError(Exception e, Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        model.addAttribute(ATTRIBUTE_ERROR, e.getMessage());
        model.addAttribute(ATTRIBUTE_STATUS, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return TEMPLATE_ERROR;
    }

    /**
     * Обрабатывает непредвиденные исключения.
     *
     * @param e        исключение типа Exception
     * @param model    модель
     * @param response ответ
     * @return имя шаблона ошибки
     */
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        model.addAttribute(ATTRIBUTE_ERROR, e.getMessage());
        model.addAttribute(ATTRIBUTE_STATUS, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return TEMPLATE_ERROR;
    }
}