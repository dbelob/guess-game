package guess;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static junit.framework.TestCase.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AppTests {
    @Test
    public void contextLoads() {
        assertTrue(true);
    }
}
