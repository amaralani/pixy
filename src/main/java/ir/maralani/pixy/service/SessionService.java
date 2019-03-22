package ir.maralani.pixy.service;

import ir.maralani.pixy.entity.Session;
import ir.maralani.pixy.exception.StreamProcessingException;

import java.util.List;
import java.util.Optional;

/**
 * @author amir
 * <p>
 * Session related services
 */
public interface SessionService {

    /**
     * This method processes streams and persists the result.
     * Server and client streams are processed separately and joined together as a {@link ir.maralani.pixy.entity.Session}
     *
     * @param clientStream contains client stream instructions. Should not be null.
     * @param serverStream contains server stream instructions. Should not be null.
     * @throws StreamProcessingException when processing fails due to malformed instructions.
     */
    void processStreams(String clientStream, String serverStream) throws StreamProcessingException;

    List<String> getAllSessionIds();

    Optional<Session> getSessionById(String sessionId);
}
