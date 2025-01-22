package jp.speakbuddy.audiovault.exception;

// Custom exception for resource not found
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}