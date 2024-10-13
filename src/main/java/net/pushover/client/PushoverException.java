package net.pushover.client;

import java.io.Serial;

public class PushoverException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public PushoverException(String message, Throwable cause) {
        super(message, cause);
    }
}
