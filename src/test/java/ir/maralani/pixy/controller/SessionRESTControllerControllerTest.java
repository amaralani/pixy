package ir.maralani.pixy.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ir.maralani.pixy.entity.Session;
import ir.maralani.pixy.entity.SessionResponseDTO;
import ir.maralani.pixy.repository.SessionRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

public class SessionRESTControllerControllerTest extends AbstractControllerTest {

    private String SESSION_CONTENT = "{\"client_stream\": \"3.key,3.101,1.0;4.sync,10.4318030518;3.key,3.102,1.1\",\"server_stream\": \"3.key,3.101,1.0;4.sync,10.4318030518;3.key,3.102,1.1\"}";
    @Autowired
    private SessionRepository sessionRepository;
    private List<String> sessionsToRemove = new ArrayList<>();

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @After
    public void after() {
        cleanUp();
    }

    @Test
    public void postSessionAuthenticationTest() throws Exception {
        List<Session> sessions = sessionRepository.findAll(); // for further cleanup
        String uri = "/session";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .content(SESSION_CONTENT)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals("No authentication is provided, should response with http 401", 401, status);
        mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .content(SESSION_CONTENT)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", generateRandomAuthenticationHeader()))
                .andReturn();
        status = mvcResult.getResponse().getStatus();
        assertEquals("Bad credential is provided, should response with http 401", 401, status);
        mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .content(SESSION_CONTENT)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .with(user(username).password(password))).andReturn();
        status = mvcResult.getResponse().getStatus();
        assertEquals("Good credential is provided, should response with http 200", 200, status);
        List<Session> newSessions = sessionRepository.findAll();
        newSessions.removeAll(sessions);
        for (Session session : newSessions) {
            sessionsToRemove.add(session.getId());
        }
    }

    @Test
    public void postSessionTest() throws Exception {
        List<Session> sessions = sessionRepository.findAll();
        String uri = "/session";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .content(SESSION_CONTENT)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(user(username).password(password)))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals("Good credential is provided, should authenticate successfully", 200, status);
        List<Session> newSessions = sessionRepository.findAll();
        assertTrue("At least one new session is expected", newSessions.size() > sessions.size());
        newSessions.removeAll(sessions);
        boolean targetSessionExists = false;
        for (Session session : newSessions) {
            targetSessionExists = session.getClientContent()
                    .contains("{\"sync\":1,\"key\":2}") && session.getServerContent()
                    .contains("{\"sync\":1,\"key\":2}");
            sessionsToRemove.add(session.getId());
        }
        assertTrue("target session is not inserted in db", targetSessionExists);

        // Bad request #1
        mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .content("Unparsable content")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(user(username).password(password)))
                .andReturn();
        status = mvcResult.getResponse().getStatus();
        assertEquals("Unparsable Content, should return 400", 400, status);
        assertEquals("Unparsable Content, should return error", "Error Parsing Request", mvcResult.getResponse().getContentAsString());

        // Bad request #2
        String SESSION_CONTENT_UNPROCESSABLE = "{\"client_stream\": \"key,3.101,1.0;4.sync,10.4318030518;3.key,3.102,1.1; ;\",\"server_stream\": \"3.key,3.101,1.0;4.sync,10.4318030518;3.key,3.102,1.1\"}";
        mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .content(SESSION_CONTENT_UNPROCESSABLE)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(user(username).password(password)))
                .andReturn();
        status = mvcResult.getResponse().getStatus();
        assertEquals("Unprocessable Content, should return 400", 400, status);
        assertEquals("Unprocessable Content, should return error", "Error processing Request", mvcResult.getResponse().getContentAsString());

    }

    @Test
    public void getSessionsTest() throws Exception {
        List<Session> sessions = sessionRepository.findAll();
        String uri = "/sessions";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(user(username).password(password)))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals("Good credential is provided, should authenticate successfully", 200, status);
        Gson gson = new Gson();
        List<String> list = gson.fromJson(mvcResult.getResponse().getContentAsString(), new TypeToken<List<String>>() {
        }.getType());
        // Repository Results must be the same size as http results.
        assertEquals("Returned sessions are not the same count as DB sessions", sessions.size(), list.size());
        // Check for content
        for (Session session : sessions) {
            assertTrue("Item does not exist in list!", list.contains(session.getId()));
        }
    }

    @Test
    public void getSessionByIdTest() throws Exception {
        String clientContent = "{\"sync\":1,\"key\":2}";
        String serverContent = "{\"sync\":1,\"key\":2}";
        Session session = Session.builder().clientContent(clientContent).serverContent(serverContent).build();
        session = sessionRepository.save(session);
        sessionsToRemove.add(session.getId());
        String uri = "/session/" + session.getId();
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(user(username).password(password)))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals("Good credential is provided, should authenticate successfully", 200, status);
        assertFalse("Empty Result", StringUtils.isEmpty(mvcResult.getResponse().getContentAsString()));
        Gson gson = new Gson();
        SessionResponseDTO sessionResponseDTO = gson.fromJson(mvcResult.getResponse().getContentAsString(), SessionResponseDTO.class);
        assertEquals(clientContent, gson.toJson(sessionResponseDTO.getClient_statistics()));
        assertEquals(serverContent, gson.toJson(sessionResponseDTO.getServer_statistics()));

        uri = "/session/" + generateRandomString();
        mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(user(username).password(password)))
                .andReturn();
        status = mvcResult.getResponse().getStatus();
        assertEquals("Wrong session id, should return bad request", 400, status);

    }

    @Override
    public void cleanUp() {
        for (String id : sessionsToRemove) {
            sessionRepository.deleteById(id);
        }
    }
}
