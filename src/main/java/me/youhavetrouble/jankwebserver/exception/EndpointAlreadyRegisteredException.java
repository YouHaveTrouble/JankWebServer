package me.youhavetrouble.jankwebserver.exception;

public class EndpointAlreadyRegisteredException extends RuntimeException {

    public EndpointAlreadyRegisteredException(String message) {
        super(message);
    }

}
