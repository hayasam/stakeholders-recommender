package upc.stakeholdersrecommender.domain.Schemas;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectKeywordSchemaTest {

    @Test
    public void testGetRequirements() {
        System.out.println("getRequirements");
        ProjectKeywordSchema instance = new ProjectKeywordSchema();
        List<KeywordReturnSchema> expResult = null;
        List<KeywordReturnSchema> result = instance.getRequirements();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetRequirements() {
        System.out.println("setRequirements");
        List<KeywordReturnSchema> requirements = null;
        ProjectKeywordSchema instance = new ProjectKeywordSchema();
        instance.setRequirements(requirements);
        assertEquals(null, instance.getRequirements());

    }

    @Test
    public void testGetProjectId() {
        System.out.println("getProjectId");
        ProjectKeywordSchema instance = new ProjectKeywordSchema();
        instance.setProjectId("1");
        String expResult = "1";
        String result = instance.getProjectId();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetProjectId() {
        System.out.println("setProjectId");
        String projectId = "1";
        ProjectKeywordSchema instance = new ProjectKeywordSchema();
        instance.setProjectId(projectId);
        assertEquals(projectId, instance.getProjectId());
    }
    
}
