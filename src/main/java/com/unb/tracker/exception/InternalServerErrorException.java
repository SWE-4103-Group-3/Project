package com.unb.tracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String s) {
        super(s);
    }

    public InternalServerErrorException() {

    }
}
