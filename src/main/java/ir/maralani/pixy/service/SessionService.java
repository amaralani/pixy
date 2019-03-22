package ir.maralani.pixy.service;

import ir.maralani.pixy.exception.StreamProcessingException;
import org.springframework.stereotype.Service;

/**
 * @author amir
 *
 * Session related services
 */
public interface SessionService {

    /**
     * This method processes streams and persists the result.
     * Server and client streams are processed separately and joined together as a {@link ir.maralani.pixy.entity.Session}
     * @param clientStream contains client stream instruction. Should not be null.
     * @param serverStream contains server stream instruction. Should not be null.
     * @throws StreamProcessingException when processing fails due to malformed streams.
     */
    void processStreams(String clientStream, String serverStream) throws StreamProcessingException;
}
