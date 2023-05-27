package com.vodafone.errorhandlling;

import org.springframework.http.HttpStatus;

public class ConflictException extends APIException{
    public ConflictException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }
}
