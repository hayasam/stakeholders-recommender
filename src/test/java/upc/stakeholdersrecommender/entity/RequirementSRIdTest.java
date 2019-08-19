package upc.stakeholdersrecommender.entity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RequirementSRIdTest {

    @Test
    public void testGetprojectId() {
        System.out.println("getprojectId");
        RequirementSRId instance = new RequirementSRId();
        instance.setProjectId("12");
        String expResult = "12";
        String result = instance.getprojectId();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetRequirementId() {
        System.out.println("getRequirementId");
        RequirementSRId instance = new RequirementSRId();
        instance.setRequirementId("1");
        String expResult = "1";
        String result = instance.getRequirementId();
        assertEquals(expResult, result);
    }

    @Test
    public void testEquals() {
        System.out.println("equals");
        Object o = null;
        RequirementSRId instance = new RequirementSRId();
        boolean expResult = false;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);
    }


    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        RequirementSRId instance = new RequirementSRId();
        int expResult = new RequirementSRId().hashCode();
        int result = instance.hashCode();
        assertEquals(expResult, result);
    }

}
