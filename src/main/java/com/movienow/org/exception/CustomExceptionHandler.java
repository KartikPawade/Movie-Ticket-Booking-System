package com.movienow.org.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.Timestamp;
import java.util.Date;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {


    @ExceptionHandler(value = UnauthorizedException.class)
    public ResponseEntity<ExceptionResponse> unauthorized(UnauthorizedException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.UNAUTHORIZED);
        exceptionResponse.setMessage(ex.getMessage());
        return buildResponseEntity(exceptionResponse);
    }

    @ExceptionHandler(value = ForbiddenException.class)
    public ResponseEntity<ExceptionResponse> forbiddenException(ForbiddenException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.FORBIDDEN);
        exceptionResponse.setMessage(ex.getMessage());
        return buildResponseEntity(exceptionResponse);
    }


    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ExceptionResponse> objectNotFound(NotFoundException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.NOT_FOUND);
        exceptionResponse.setMessage(ex.getMessage());
        return buildResponseEntity(exceptionResponse);
    }

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequest(BadRequestException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST);
        exceptionResponse.setMessage(e.getMessage());
        return buildResponseEntity(exceptionResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> accessDeniedException(AccessDeniedException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.FORBIDDEN);
        exceptionResponse.setMessage(ex.getMessage());
        return buildResponseEntity(exceptionResponse);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ExceptionResponse> accessDeniedException(Exception ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        exceptionResponse.setMessage(ex.getMessage());
        return buildResponseEntity(exceptionResponse);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> accessDeniedException(ConstraintViolationException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST);
        exceptionResponse.setMessage(ex.getMessage());
        return buildResponseEntity(exceptionResponse);
    }

    /**
     * Used to return exception response
     * @param exceptionResponse
     * @return
     */
    private ResponseEntity<ExceptionResponse> buildResponseEntity(ExceptionResponse exceptionResponse) {
        exceptionResponse.setTimestamp(getDateTime());
        return new ResponseEntity<>(exceptionResponse, exceptionResponse.getStatus());
    }
    private static Timestamp getDateTime() {
        Date date = new Date();
        return new Timestamp(date.getTime());
    }
}
