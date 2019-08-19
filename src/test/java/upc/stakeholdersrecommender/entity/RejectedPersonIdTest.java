package upc.stakeholdersrecommender.entity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RejectedPersonIdTest {


    @Test
    public void testGetprojectId() {
        System.out.println("getprojectId");
        RejectedPersonId instance = new RejectedPersonId();
        instance.setPersonId("1");
        String expResult = "1";
        String result = instance.getPersonId();
        assertEquals(expResult, result);
    }


    @Test
    public void testGetOrganizationId() {
        System.out.println("getPersonId");
        RejectedPersonId instance = new RejectedPersonId();
        instance.setOrganizationId("Res");
        String expResult = "Res";
        String result = instance.getOrganizationId();
        assertEquals(expResult, result);
    }


    @Test
    public void testEquals() {
        System.out.println("equals");
        Object o = null;
        RejectedPersonId instance = new RejectedPersonId();
        boolean expResult = false;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);
    }

    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        RejectedPersonId instance = new RejectedPersonId();
        int expResult = new RejectedPersonId().hashCode();
        int result = instance.hashCode();
        assertEquals(expResult, result);
    }

}