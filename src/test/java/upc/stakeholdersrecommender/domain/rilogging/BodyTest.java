package upc.stakeholdersrecommender.domain.rilogging;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BodyTest {

    @Test
    public void createTest(){
        Body bod=new Body();
        assertTrue(bod!=null);
    }
    @Test
    public void getValueTest() {
        Body bod=new Body();
        bod.setValue("val");
        assertTrue(bod.getValue().equals("val"));
    }
    @Test
    public void getSrcElementclassNameTest() {
        Body bod=new Body();
        bod.setSrcElementclassName("val");
        assertTrue(bod.getSrcElementclassName().equals("val"));
    }
    @Test
    public void getUsernameTest() {
        Body bod=new Body();
        bod.setUsername("val");
        assertTrue(bod.getUsername().equals("val"));
    }
    @Test
    public void getUserIdTest() {
        Body bod=new Body();
        bod.setUserId("val");
        assertTrue(bod.getUserId().equals("val"));
    }
    @Test
    public void getRequirementIdTest() {
        Body bod=new Body();
        bod.setRequirementId("val");
        assertTrue(bod.getRequirementId().equals("val"));
    }
    @Test
    public void getTimestampTest() {
        Body bod=new Body();
        bod.setTimestamp("val");
        assertTrue(bod.getTimestamp().equals("val"));
    }
    @Test
    public void getProjectIdTest() {
        Body bod=new Body();
        bod.setProjectId("val");
        assertTrue(bod.getProjectId().equals("val"));
    }
    @Test
    public void getInnerTextTest() {
        Body bod=new Body();
        bod.setInnerText("val");
        assertTrue(bod.getInnerText().equals("val"));
    }
    @Test
    public void getUnixTimeTest() {
        Body bod=new Body();
        bod.setUnixTime(12);
        assertTrue(bod.getUnixTime()==12);
    }




}
