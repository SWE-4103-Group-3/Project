package com.unb.tracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class BadRequestException extends RuntimeException {
    public BadRequestException(String s) {
        super(s);
    }

    public BadRequestException() {

    }
}
