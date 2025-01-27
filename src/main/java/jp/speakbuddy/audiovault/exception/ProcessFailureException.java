package jp.speakbuddy.audiovault.exception;

// Custom exception for process failure
public class ProcessFailureException extends RuntimeException {
    public ProcessFailureException(String message) {
        super(message);
    }
}