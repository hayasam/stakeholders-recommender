package upc.stakeholdersrecommender.domain.rilogging;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LogTest {

    @Test
    public void LogTest() {
        Log log = new Log();
        assertTrue(log != null);
    }

    @Test
    public void getBodyTest() {
        Log log = new Log();
        Body bo = new Body();
        log.setBody(bo);
        assertTrue(log.getBody() == bo);
    }

    @Test
    public void getHeaderTest() {
        Log log = new Log();
        Header bo = new Header();
        log.setHeader(bo);
        assertTrue(log.getHeader() == bo);
    }

    @Test
    public void getUnixTimeTest() {
        Log log = new Log();
        Header bo = new Header();
        log.setHeader(bo);
        assertTrue(log.getHeader() == bo);

    }

    @Test
    public void getEvent_type() {
        Log log = new Log();
        log.setEvent_type("ev");
        assertTrue(log.getEvent_type().equals("ev"));

    }

    @Test
    public void getDescriptionOrNameTest() {
        Log log = new Log();
        Body bo = new Body();
        bo.setInnerText("name");
        log.setBody(bo);
        assertTrue(log.getDescriptionOrName().equals("name"));
        bo.setInnerText(null);
        bo.setValue("name2");
        log.setBody(bo);
        assertTrue(log.getDescriptionOrName().equals("name2"));
        bo.setValue(null);
        log.setBody(bo);
        assertTrue(log.getDescriptionOrName().equals(""));
    }


    @Test
    public void isDescriptionTrue() {
        Log log = new Log();
        Body bo = new Body();
        bo.setSrcElementclassName("note-editable");
        log.setBody(bo);
        assertTrue(log.isDescription());
        bo.setSrcElementclassName("note-editable or-description-active");
        log.setBody(bo);
        assertTrue(log.isDescription());
    }

    @Test
    public void isDescriptionFalse() {
        Log log = new Log();
        Body bo = new Body();
        bo.setSrcElementclassName("fail");
        log.setBody(bo);
        assertTrue(!log.isDescription());
    }

    @Test
    public void isNameFalse() {
        Log log = new Log();
        Body bo = new Body();
        bo.setSrcElementclassName("thing");
        log.setBody(bo);
        assertTrue(!log.isName());
    }

    @Test
    public void isNameTrue() {
        Log log = new Log();
        Body bo = new Body();
        bo.setSrcElementclassName("or-requirement-title form-control");
        log.setBody(bo);
        assertTrue(log.isName());
    }
}