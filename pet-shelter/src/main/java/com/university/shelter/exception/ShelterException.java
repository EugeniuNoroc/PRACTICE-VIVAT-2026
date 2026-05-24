package com.university.shelter.exception;

public class ShelterException extends RuntimeException{
    public ShelterException(String msg){
        super(msg);
    }
    public ShelterException(String msg, Throwable cause){
        super(msg, cause);
    }
}
