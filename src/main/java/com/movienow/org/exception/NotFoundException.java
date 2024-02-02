package com.movienow.org.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = -701347827920139197L;
    private final String message;
    private final HttpStatus status;

    public NotFoundException(String message) {
        super(message);
        this.message = message;
        this.status = HttpStatus.NOT_FOUND;
    }
}