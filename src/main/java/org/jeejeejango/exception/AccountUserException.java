package org.jeejeejango.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author jeejeejango
 * @since 30/01/2019 12:45 AM
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Account's user cannot be changed")
public class AccountUserException extends RuntimeException {

    public AccountUserException() {
    }


    public AccountUserException(String message) {
        super(message);
    }


    public AccountUserException(String message, Throwable cause) {
        super(message, cause);
    }


    public AccountUserException(Throwable cause) {
        super(cause);
    }


    public AccountUserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


}
