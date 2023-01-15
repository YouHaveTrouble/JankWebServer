package me.youhavetrouble.jankwebserver.exception;

public class NotDirectoryException extends IllegalArgumentException {

    public NotDirectoryException(String message) {
        super(message);
    }

}
