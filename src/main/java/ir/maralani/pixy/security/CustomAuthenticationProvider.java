package ir.maralani.pixy.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

@Configuration
@PropertySource("classpath:security.properties")
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private Environment env;

    /**
     * Custom Authentication method.
     * Finds username and password from both environment variables and property file.
     *
     * @param authentication is the provided {@link Authentication}
     * @return a {@link UsernamePasswordAuthenticationToken} if successful. Else return null (failed authentication).
     */
    @Override
    public Authentication authenticate(Authentication authentication) {
        // Checks both environment variables and properties in security.properties
        String password = env.getProperty(authentication.getName());
        if (!StringUtils.isEmpty(password) && password.equals(authentication.getCredentials())) {
            return new UsernamePasswordAuthenticationToken(authentication.getName(), password, new ArrayList<>());
        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
