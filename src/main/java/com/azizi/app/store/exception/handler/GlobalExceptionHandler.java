package com.azizi.app.store.exception.handler;

import com.azizi.app.store.exception.base.NotFoundException;
import com.azizi.app.store.exception.model.Error;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public Error handle(NotFoundException ex, WebRequest request) {
        log.trace("Resource not found {}", ex.getMessage(), ex);
        return handleError(request, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Error handle(HttpMessageNotReadableException ex, WebRequest request) {
        log.trace("Request is invalid format {}", ex.getMessage(), ex);
        return handleError(request, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Error handle(MethodArgumentTypeMismatchException ex, WebRequest request) {
        log.trace("Method arguments are not valid {}", ex.getMessage(), ex);
        return handleError(request, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestHeaderException.class)
    public Error handle(MissingRequestHeaderException ex, WebRequest request) {
        log.trace("Missing request header {}", ex.getMessage(), ex);
        return handleError(request, HttpStatus.BAD_REQUEST, ex.getMessage());

    }

    private Error handleError(WebRequest request, HttpStatus status, String message) {
        return Error.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                .error(status.getReasonPhrase())
                .message(message)
                .path(((ServletWebRequest) request).getRequest().getRequestURI())
                .build();
    }

}


