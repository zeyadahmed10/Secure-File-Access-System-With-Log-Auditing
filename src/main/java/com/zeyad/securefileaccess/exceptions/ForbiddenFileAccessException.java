package com.zeyad.securefileaccess.exceptions;

public class ForbiddenFileAccessException extends RuntimeException {
    public ForbiddenFileAccessException() {
    }

    public ForbiddenFileAccessException(String message) {
        super(message);
    }

    public ForbiddenFileAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForbiddenFileAccessException(Throwable cause) {
        super(cause);
    }

    public ForbiddenFileAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
