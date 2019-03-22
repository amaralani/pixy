package ir.maralani.pixy.controller;

import ir.maralani.pixy.AbstractTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@WebAppConfiguration
@PropertySource("classpath:security.properties")
public abstract class AbstractControllerTest extends AbstractTest {
    MockMvc mvc;
    @Autowired
    WebApplicationContext webApplicationContext;

    String username = "testusername";
    String password = "testpassword";

    String generateRandomAuthenticationHeader() {
        return "Basic " + generateRandomString() + ":" + generateRandomString();
    }

    String generateRandomString() {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    protected void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
    }
}