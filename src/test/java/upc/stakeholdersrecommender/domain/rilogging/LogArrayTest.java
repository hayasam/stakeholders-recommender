package upc.stakeholdersrecommender.domain.rilogging;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LogArrayTest {

    @Test
    public void LogArrayTest() {
        LogArray logArr = new LogArray();
        assertTrue(logArr != null);
    }

    @Test
    public void getLogsTest() {
        LogArray logArr = new LogArray();
        List<Log> logs = new ArrayList<>();
        logArr.setLogs(logs);
        assertTrue(logArr.getLogs() == logs);

    }

}
