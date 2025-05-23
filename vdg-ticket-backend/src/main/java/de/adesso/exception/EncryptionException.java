package de.adesso.exception;

public class EncryptionException extends Exception{

    private static final long serialVersionUID = 1L;

    public EncryptionException(String errorMessage) {
        super(errorMessage);
    }
}
