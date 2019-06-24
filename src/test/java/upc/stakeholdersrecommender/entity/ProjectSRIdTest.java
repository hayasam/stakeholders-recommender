
package upc.stakeholdersrecommender.entity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectSRIdTest {


    @Test
    public void testGetprojectId() {
        System.out.println("getprojectId");
        ProjectSRId instance = new ProjectSRId();
        instance.setProjectId("1");
        String expResult = "1";
        String result = instance.getProjectId();
        assertEquals(expResult, result);
    }


    @Test
    public void testGetOrganizationId() {
        System.out.println("getPersonId");
        ProjectSRId instance = new ProjectSRId();
        instance.setOrganizationId("Res");
        String expResult = "Res";
        String result = instance.getOrganizationId();
        assertEquals(expResult, result);
    }


    @Test
    public void testEquals() {
        System.out.println("equals");
        Object o = null;
        ProjectSRId instance = new ProjectSRId();
        boolean expResult = false;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);
    }

    @Test
    public void testEqualsSame() {
        System.out.println("equals");
        ProjectSRId instance = new ProjectSRId();
        Object o = instance;
        boolean expResult = true;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);
    }

    @Test
    public void testEqualsSameAttritubtes() {
        System.out.println("equals");
        ProjectSRId instance = new ProjectSRId();
        instance.setProjectId("1");
        instance.setOrganizationId("1");
        Object o = new ProjectSRId();
        ((ProjectSRId) o).setProjectId("1");
        ((ProjectSRId) o).setOrganizationId("1");
        boolean expResult = true;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);
    }


    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        ProjectSRId instance = new ProjectSRId();
        int expResult =new ProjectSRId().hashCode();
        int result = instance.hashCode();
        assertEquals(expResult, result);
    }

}