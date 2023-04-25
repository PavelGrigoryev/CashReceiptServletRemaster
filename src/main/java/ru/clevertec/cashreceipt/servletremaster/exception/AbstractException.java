package ru.clevertec.cashreceipt.servletremaster.exception;

public abstract class AbstractException extends RuntimeException {

    protected AbstractException(String message) {
        super(message);
    }

}
