package de.adesso.exception;

public class TicketException extends Exception{

    private static final long serialVersionUID = 1L;

    public TicketException(String message){
        super(message);
    }
}
