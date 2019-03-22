package ir.maralani.pixy.service.impl;

import com.google.gson.Gson;
import ir.maralani.pixy.entity.Session;
import ir.maralani.pixy.exception.StreamProcessingException;
import ir.maralani.pixy.repository.SessionRepository;
import ir.maralani.pixy.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author amir
 *
 * Implementation of Session Services.
 */
@Slf4j
@Service
public class SessionServiceImpl implements SessionService {

    @Autowired
    SessionRepository sessionRepository;

    private Gson gson = new Gson();

    @Override
    public void processStreams(String clientStream, String serverStream) throws StreamProcessingException {
        Map<String, Integer> clientInstructionCount;
        Map<String, Integer> serverInstructionCount;
        try {
            clientInstructionCount = getInstructionCounterMapFromStream(clientStream);
            serverInstructionCount = getInstructionCounterMapFromStream(serverStream);
        } catch (Exception ex) {
            log.error("Exception when processing request", ex);
            throw new StreamProcessingException("Error processing Request");
        }

        // save result in db
        Session session = Session.builder()
                .clientContent(gson.toJson(clientInstructionCount))
                .serverContent(gson.toJson(serverInstructionCount))
                .build();
        sessionRepository.save(session);
    }

    /**
     * Break each stream into instructions, then for each instruction find the OPCODE and update the counter.
     * @param stream is a stream of instructions.
     * @return a map which each key is OPCODE and value is the related count.
     */
    private Map<String, Integer> getInstructionCounterMapFromStream(String stream) {
        Map<String, Integer> instructionCount = new HashMap<>();
        String[] streamInstructionSets = stream.split(";");
        for (String streamInstructionSet : streamInstructionSets) {
            String instruction = streamInstructionSet.split(",")[0].split("\\.")[1];
            instructionCount.put(instruction, instructionCount.getOrDefault(instruction, 0) + 1);
        }
        return instructionCount;
    }

}
