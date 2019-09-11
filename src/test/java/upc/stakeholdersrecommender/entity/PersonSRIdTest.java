package upc.stakeholdersrecommender.entity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonSRIdTest {


    @Test
    public void testGetprojectId() {
        System.out.println("getprojectId");
        PersonSRId instance = new PersonSRId();
        instance.setProjectId("1");
        String expResult = "1";
        String result = instance.getProjectId();
        assertEquals(expResult, result);
    }


    @Test
    public void testGetPersonId() {
        System.out.println("getPersonId");
        PersonSRId instance = new PersonSRId();
        instance.setPersonId("Res");
        String expResult = "Res";
        String result = instance.getPersonId();
        assertEquals(expResult, result);
    }


    @Test
    public void testEquals() {
        System.out.println("equals");
        Object o = null;
        PersonSRId instance = new PersonSRId();
        boolean expResult = false;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);
    }


    @Test
    public void testEqualsSame() {
        System.out.println("equals");
        PersonSRId instance = new PersonSRId();
        Object o = instance;
        boolean expResult = true;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);
    }

    @Test
    public void testEqualsSameAttritubtes() {
        System.out.println("equals");
        PersonSRId instance = new PersonSRId();
        instance.setPersonId("1");
        instance.setProjectId("1");
        instance.setOrganizationId("1");
        Object o = new PersonSRId();
        ((PersonSRId) o).setPersonId("1");
        ((PersonSRId) o).setProjectId("1");
        ((PersonSRId) o).setOrganizationId("1");
        boolean expResult = true;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);
    }


    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        PersonSRId instance = new PersonSRId();
        int expResult = new PersonSRId().hashCode();
        int result = instance.hashCode();
        assertEquals(expResult, result);
    }

}
