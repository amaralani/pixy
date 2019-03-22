package ir.maralani.pixy.exception;

/**
 * @author amir
 *
 * Semi-general exception to wrap processing exceptions.
 */
public class StreamProcessingException extends Exception {

    public StreamProcessingException(String message) {
        super(message);
    }
}
