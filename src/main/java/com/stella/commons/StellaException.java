package com.stella.commons;

public class StellaException extends RuntimeException {

    private static final long serialVersionUID = 5171739977852743659L;

    public StellaException() {
        super();
    }

    public StellaException(String message) {
        super(message);
    }

    public StellaException(String message, Throwable cause) {
        super(message, cause);
    }

    public StellaException(Throwable cause) {
        super(cause);
    }

    protected StellaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
