package ir.maralani.pixy.repository;

import ir.maralani.pixy.AbstractTest;
import ir.maralani.pixy.entity.Session;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SessionRepositorySanityTest extends AbstractTest {
    @Autowired
    private SessionRepository sessionRepository;
    private List<String> sessionsToRemove = new ArrayList<>();

    @Test
    public void testSaveSession() {
        List<Session> sessions = sessionRepository.findAll();
        Session session = createSession();
        List<Session> newSessions = sessionRepository.findAll();
        // Make sure there is at least one new session
        Assert.assertNotEquals(sessions.size(), newSessions.size());
        // Make sure that new session is what we inserted
        Assert.assertTrue(newSessions.contains(session));
    }

    private Session createSession() {
        Session session = Session.builder()
                .clientContent("{ \"key\" : 2 , \"mouse\" : 3}")
                .serverContent("{ \"key\" : 2 , \"mouse\" : 3}")
                .build();
        session = sessionRepository.save(session);
        sessionsToRemove.add(session.getId());
        return session;
    }

    @Test
    public void getAllSessionIdsTest() {
        Session session = createSession();
        List<String> sessionIds = sessionRepository.findAll().stream().map(Session::getId).collect(Collectors.toList());
        Assert.assertTrue(sessionIds.contains(session.getId()));
    }

    @Override
    public void cleanUp() {
        for (String id : sessionsToRemove) {
            sessionRepository.deleteById(id);
        }
    }
}
