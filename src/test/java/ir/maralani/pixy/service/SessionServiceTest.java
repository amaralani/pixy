package ir.maralani.pixy.service;

import ir.maralani.pixy.AbstractTest;
import ir.maralani.pixy.entity.Session;
import ir.maralani.pixy.exception.StreamProcessingException;
import ir.maralani.pixy.repository.SessionRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class SessionServiceTest extends AbstractTest {
    @Autowired
    private SessionService sessionService;
    @Autowired
    private SessionRepository sessionRepository;
    private List<String> sessionsToRemove = new ArrayList<>();

    @Test
    public void testProcessStreams() {
        List<Session> sessions = sessionRepository.findAll();
        String clientStream = "3.key,3.101,1.0;4.sync,10.4318030518;3.key,3.102,1.1";
        String serverStream = "3.key,3.101,1.0;4.sync,10.4318030518;3.key,3.102,1.1";
        try {
            sessionService.processStreams(clientStream, serverStream);
            List<Session> newSessions = sessionRepository.findAll();
            assertTrue("At least one new session is expected", newSessions.size() > sessions.size());
            newSessions.removeAll(sessions);
            boolean targetSessionExists = false;
            for (Session session : newSessions) {
                targetSessionExists = session.getClientContent().contains("{\"sync\":1,\"key\":2}")
                        && session.getServerContent().contains("{\"sync\":1,\"key\":2}");
                sessionsToRemove.add(session.getId());
            }
            assertTrue("target session is not inserted in db", targetSessionExists);
        } catch (StreamProcessingException e) {
            Assert.fail("Exception is not expected");
        }

        // Try invalid client stream

        // There are two problems with unprocessable streams :
        // 1. first instruction is not in proper format (size.value)
        // 2. last instruction is empty
        sessions = sessionRepository.findAll();
        try {
            String unprocessableClientStream = "key,3.101,1.0;4.sync,10.4318030518;3.key,3.102,1.1; ;";
            sessionService.processStreams(unprocessableClientStream, serverStream);
            Assert.fail("Invalid client stream, Exception is expected");
        } catch (StreamProcessingException e) {
            List<Session> newSessions = sessionRepository.findAll();
            assertEquals("Session size after processing invalid client stream does not match", sessions.size(), newSessions.size());
        }

        // Try invalid server stream
        sessions = sessionRepository.findAll();
        try {
            String unprocessableServerStream = "key,3.101,1.0;4.sync,10.4318030518;3.key,3.102,1.1; ;";
            sessionService.processStreams(clientStream, unprocessableServerStream);
            Assert.fail("Invalid server stream, Exception is expected");
        } catch (StreamProcessingException e) {
            List<Session> newSessions = sessionRepository.findAll();
            assertEquals("Session size after processing invalid server stream does not match", sessions.size(), newSessions.size());
        }
    }

    @Test
    public void testGetAllSessionIds() {
        Session session = Session.builder().clientContent("testClientContent").serverContent("testServerContent").build();
        session = sessionRepository.save(session);
        sessionsToRemove.add(session.getId());
        List<Session> sessionsOfRepository = sessionRepository.findAll();
        List<String> sessionIds = sessionService.getAllSessionIds();
        for (Session sessionItem : sessionsOfRepository) {
            assertTrue("Item does not exist in list!", sessionIds.contains(sessionItem.getId()));
        }
        assertTrue("newly added session is not present", sessionIds.contains(session.getId()));
    }

    @Test
    public void getSessionByIdTest() {
        // Null session id
        Optional<Session> session = sessionService.getSessionById(null);
        Assert.assertFalse("null session id, optional should not be present", session.isPresent());
        // Blank session id
        session = sessionService.getSessionById("");
        Assert.assertFalse("blank session id, optional should not be present", session.isPresent());
        // Empty Session Id
        session = sessionService.getSessionById(" ");
        Assert.assertFalse("empty session id, optional should not be present", session.isPresent());
        // Invalid Session Id
        session = sessionService.getSessionById("thisIsNotAUuid");
        Assert.assertFalse("invalid session id, optional should not be present", session.isPresent());


        Session newSession = Session.builder().clientContent("testClientContent").serverContent("testServerContent").build();
        newSession = sessionRepository.save(newSession);
        sessionsToRemove.add(newSession.getId());
        session = sessionService.getSessionById(newSession.getId());
        Assert.assertTrue("Valid session id, optional must be present", session.isPresent());
    }

    @Override
    public void cleanUp() {
        for (String id : sessionsToRemove) {
            sessionRepository.deleteById(id);
        }
    }
}
