package ir.maralani.pixy;

import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public abstract class AbstractTest {
    public abstract void cleanUp();

    @After
    public void after(){
        cleanUp();
    }
}
