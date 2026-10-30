package br.edu.ufcspa.sgfh.domain.exception;

public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
