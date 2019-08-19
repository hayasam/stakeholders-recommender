package upc.stakeholdersrecommender.domain.rilogging;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HeaderTest {
    @Test
    public void HeaderTest() {
        Header hed = new Header();
        assertTrue(hed != null);
    }

    @Test
    public void getSessionIdTest() {
        Header hed = new Header();
        hed.setSessionid("id");
        assertTrue(hed.getSessionid().equals("id"));
    }
}