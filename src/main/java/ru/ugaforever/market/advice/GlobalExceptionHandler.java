package ru.ugaforever.market.advice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class GlobalExceptionHandler {

    // ==================== 404 ОШИБКИ ====================

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(Exception ex, Model model, HttpServletRequest request) {
        model.addAttribute("error", "Страница не найдена");
        model.addAttribute("message", "Запрашиваемая страница не существует");
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("status", 404);
        return "error/404";
    }

    // ==================== 400 ОШИБКИ (BAD REQUEST) ====================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationExceptions(MethodArgumentNotValidException ex, Model model) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        model.addAttribute("error", "Ошибка валидации");
        model.addAttribute("message", "Проверьте правильность заполнения полей");
        model.addAttribute("errors", errors);
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("status", 400);
        return "error/400";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException ex, Model model) {
        model.addAttribute("error", "Неверный тип параметра");
        model.addAttribute("message", String.format("Параметр '%s' должен быть типа %s",
                ex.getName(), ex.getRequiredType().getSimpleName()));
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("status", 400);
        return "error/400";
    }

    // ==================== 404 ДЛЯ ENTITY ====================

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFound(EntityNotFoundException ex, Model model, HttpServletRequest request) {
        model.addAttribute("error", "Запись не найдена");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("status", 404);
        return "error/404";
    }

    // ==================== 500 ОШИБКИ СЕРВЕРА ====================

    @ExceptionHandler({SQLException.class, DataAccessException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDatabaseError(Exception ex, Model model) {
        model.addAttribute("error", "Ошибка базы данных");
        model.addAttribute("message", "Произошла ошибка при работе с базой данных");
        model.addAttribute("technical", ex.getMessage());
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("status", 500);
        return "error/500";
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleNullPointer(NullPointerException ex, Model model, HttpServletRequest request) {
        model.addAttribute("error", "Внутренняя ошибка сервера");
        model.addAttribute("message", "Произошла непредвиденная ошибка (NullPointerException)");
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("status", 500);

        // Логируем для отладки
        ex.printStackTrace();

        return "error/500";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
        model.addAttribute("error", "Некорректный аргумент");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("status", 500);
        return "error/500";
    }

    // ==================== ОБЩИЙ ОБРАБОТЧИК (все остальные ошибки) ====================

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericError(Exception ex, Model model, HttpServletRequest request) {
        model.addAttribute("error", "Внутренняя ошибка сервера");
        model.addAttribute("message", "Произошла непредвиденная ошибка");
        model.addAttribute("technical", ex.getMessage());
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("status", 500);

        // Логируем для отладки
        ex.printStackTrace();

        return "error/500";
    }

    // ==================== АТРИБУТЫ ДЛЯ ВСЕХ СТРАНИЦ (опционально) ====================

    @ModelAttribute
    public void addCommonAttributes(Model model) {
        model.addAttribute("appName", "My Market App");
        model.addAttribute("currentYear", LocalDateTime.now().getYear());
    }
}

