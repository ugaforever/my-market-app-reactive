/*
package ru.ugaforever.reactive.market.backend.advice;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.backend.exception.EntityNotFoundException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class ReactiveGlobalExceptionHandler {

    private static final String ERROR_VIEW_404 = "error/404";
    private static final String ERROR_VIEW_400 = "error/400";
    private static final String ERROR_VIEW_500 = "error/500";

    // ==================== 404 ОШИБКИ ====================

    @ExceptionHandler(EntityNotFoundException.class)
    public Mono<Rendering> handleEntityNotFound(EntityNotFoundException ex, ServerWebExchange exchange) {

        return Mono.just(Rendering.view(ERROR_VIEW_404)
                .modelAttribute("error", "Запись не найдена")
                .modelAttribute("message", ex.getMessage())
                .modelAttribute("path", exchange.getRequest().getURI().getPath())
                .modelAttribute("timestamp", LocalDateTime.now())
                .modelAttribute("status", HttpStatus.NOT_FOUND.value())
                .status(HttpStatus.NOT_FOUND)
                .build());
    }

    // ==================== 400 ОШИБКИ (BAD REQUEST) ====================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<Rendering> handleValidationExceptions(MethodArgumentNotValidException ex, ServerWebExchange exchange) {

        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing
                ));

        return Mono.just(Rendering.view(ERROR_VIEW_400)
                .modelAttribute("error", "Ошибка валидации")
                .modelAttribute("message", "Проверьте правильность заполнения полей")
                .modelAttribute("errors", errors)
                .modelAttribute("path", exchange.getRequest().getURI().getPath())
                .modelAttribute("timestamp", LocalDateTime.now())
                .modelAttribute("status", HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .build());
    }

    // ==================== 500 ОШИБКИ СЕРВЕРА ====================

    @ExceptionHandler({SQLException.class, DataAccessException.class})
    public Mono<Rendering> handleDatabaseError(Exception ex, ServerWebExchange exchange) {

        return Mono.just(Rendering.view(ERROR_VIEW_500)
                .modelAttribute("error", "Ошибка базы данных")
                .modelAttribute("message", "Произошла ошибка при работе с базой данных")
                .modelAttribute("technical", ex.getMessage())
                .modelAttribute("path", exchange.getRequest().getURI().getPath())
                .modelAttribute("timestamp", LocalDateTime.now())
                .modelAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build());
    }

    @ExceptionHandler(NullPointerException.class)
    public Mono<Rendering> handleNullPointerException(NullPointerException ex, ServerWebExchange exchange) {

        return Mono.just(Rendering.view(ERROR_VIEW_500)
                .modelAttribute("error", "Внутренняя ошибка сервера")
                .modelAttribute("message", "Произошла непредвиденная ошибка (NullPointerException)")
                .modelAttribute("path", exchange.getRequest().getURI().getPath())
                .modelAttribute("timestamp", LocalDateTime.now())
                .modelAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build());
    }

    // ==================== ОБЩИЙ ОБРАБОТЧИК (все остальные ошибки) ====================

    @ExceptionHandler(Exception.class)
    public Mono<Rendering> handleGenericError(Exception ex, ServerWebExchange exchange) {

        return Mono.just(Rendering.view(ERROR_VIEW_500)
                .modelAttribute("error", "Внутренняя ошибка сервера")
                .modelAttribute("message", "Произошла непредвиденная ошибка")
                .modelAttribute("technical", ex.getMessage())
                .modelAttribute("path", exchange.getRequest().getURI().getPath())
                .modelAttribute("timestamp", LocalDateTime.now())
                .modelAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build());
    }
}
*/
