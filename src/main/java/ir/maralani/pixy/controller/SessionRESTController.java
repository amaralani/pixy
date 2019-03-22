package ir.maralani.pixy.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ir.maralani.pixy.entity.Session;
import ir.maralani.pixy.entity.SessionRequestDTO;
import ir.maralani.pixy.entity.SessionResponseDTO;
import ir.maralani.pixy.exception.StreamProcessingException;
import ir.maralani.pixy.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller Class for {@link Session}
 */

@Slf4j
@RestController
public class SessionRESTController {

    private Gson gson = new Gson();
    private final SessionService sessionService;

    @Autowired
    public SessionRESTController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    /**
     * POST endpoint to receive streams.
     *
     * @param body contains server and client streams.
     * @return OK(200) if successful.
     * BadRequest(400) if request body cannot be parsed or processed.
     * InternalServerError(500) if unknown exception is thrown.
     */
    @PostMapping(value = "/session", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity receiveStreams(@RequestBody String body) {
        // Read client and server streams
        SessionRequestDTO sessionRequestDTO;
        try {
            sessionRequestDTO = gson.fromJson(body, SessionRequestDTO.class);
        } catch (Exception ex) {
            log.error("Exception when parsing request", ex);
            return ResponseEntity.badRequest().body("Error Parsing Request");
        }
        try {
            sessionService.processStreams(sessionRequestDTO.getClient_stream(), sessionRequestDTO.getServer_stream());
        } catch (StreamProcessingException e) {
            log.error("Exception when processing request", e);
            return ResponseEntity.badRequest().body("Error processing Request");
        }
        // return correct response
        return ResponseEntity.ok("Success");
        // No other exceptions are expected,if any return http status 500
    }

    /**
     * GET endpoint to send all session ids.
     *
     * @return a list of session ids, in JSON format.
     */
    @GetMapping(value = "/sessions", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getSessions() {
        List<String> sessionIds = sessionService.getAllSessionIds();
        return ResponseEntity.ok(gson.toJson(sessionIds));
    }

    /**
     * GET endpoint to find a session by its id.
     *
     * @param id is the UUID of the session.
     * @return is a {@link SessionResponseDTO} containing a {@link Session} as JSON.
     */
    @GetMapping(value = "/session/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces =
            MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getSession(@PathVariable(name = "id") String id) {
        Optional<Session> session = sessionService.getSessionById(id);
        if (!session.isPresent()) {
            return ResponseEntity.badRequest().body("Invalid Session Id");
        }
        SessionResponseDTO sessionResponseDTO = SessionResponseDTO.builder()
                .client_statistics(gson.fromJson(session.get().getClientContent(), new TypeToken<Map<String, Integer>>() {
                }.getType()))
                .server_statistics(gson.fromJson(session.get().getServerContent(), new TypeToken<Map<String, Integer>>() {
                }.getType()))
                .build();

        return ResponseEntity.ok(sessionResponseDTO);
    }

}
