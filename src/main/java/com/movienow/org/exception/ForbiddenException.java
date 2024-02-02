package com.movienow.org.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ForbiddenException extends RuntimeException {
    private final String message;
    private final HttpStatus status;

    public ForbiddenException(String message) {
        super(message);
        this.message = message;
        this.status = HttpStatus.FORBIDDEN;
    }
}