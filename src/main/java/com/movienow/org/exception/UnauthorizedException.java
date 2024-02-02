package com.movienow.org.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class UnauthorizedException extends RuntimeException {
    private final String message;
    private final String userName;
    private final HttpStatus status;

    public UnauthorizedException(String message) {
        super(message);
        this.message = message;
        this.status = HttpStatus.UNAUTHORIZED;
        this.userName = null;
    }
}