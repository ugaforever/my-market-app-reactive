package ru.ugaforever.reactive.market.backend.exception;

public class ObjectMapperException extends RuntimeException {

    public ObjectMapperException(String message) {
        super(message);
    }

    public ObjectMapperException(String message, Throwable cause) {
        super(message, cause);
    }
}
