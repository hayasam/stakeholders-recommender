package upc.stakeholdersrecommender.domain.Schemas;

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
public class RecommendReturnSchemaTest {

    @Test
    public void testGetRequirement() {
        System.out.println("getRequirement");
        RecommendReturnSchema instance = new RecommendReturnSchema();
        RequirementMinimal expResult = null;
        RequirementMinimal result = instance.getRequirement();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetRequirement() {
        System.out.println("setRequirement");
        RequirementMinimal requirement = null;
        RecommendReturnSchema instance = new RecommendReturnSchema();
        instance.setRequirement(requirement);
        assertEquals(null, instance.getRequirement());
    }

    @Test
    public void testGetPerson() {
        System.out.println("getPerson");
        RecommendReturnSchema instance =new RecommendReturnSchema();
        PersonMinimal p=new PersonMinimal();
        p.setUsername("wh");
        instance.setPerson(p);
        PersonMinimal result = instance.getPerson();
        assertEquals("wh", result.getUsername());
    }

    @Test
    public void testSetPerson() {
        System.out.println("setPerson");
        PersonMinimal person = null;
        RecommendReturnSchema instance = new RecommendReturnSchema();
        instance.setPerson(person);
        assertEquals(null, instance.getPerson());
    }

    @Test
    public void testGetAvailabilityScore() {
        System.out.println("getAvailabilityScore");
        RecommendReturnSchema instance = new RecommendReturnSchema();
        Double expResult = null;
        Double result = instance.getAvailabilityScore();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetAvailabilityScore() {
        System.out.println("setAvailabilityScore");
        Double availabilityScore = 2.0;
        RecommendReturnSchema instance = new RecommendReturnSchema();
        instance.setAvailabilityScore(availabilityScore);
        assertTrue(2.0==instance.getAvailabilityScore());
    }

    @Test
    public void testGetApropiatenessScore() {
        System.out.println("getApropiatenessScore");
        RecommendReturnSchema instance = new RecommendReturnSchema();
        Double expResult = null;
        Double result = instance.getApropiatenessScore();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetApropiatenessScore() {
        System.out.println("setApropiatenessScore");
        Double apropiatenessScore = 1.2;
        RecommendReturnSchema instance = new RecommendReturnSchema();
        instance.setApropiatenessScore(apropiatenessScore);
        assertTrue(1.2==instance.getApropiatenessScore());
    }

    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        RecommendReturnSchema a = new RecommendReturnSchema();
        PersonMinimal p=new PersonMinimal();
        RequirementMinimal r=new RequirementMinimal("1");
        p.setUsername("wh");
        a.setPerson(p);
        a.setApropiatenessScore(1.0);
        a.setAvailabilityScore(1.0);
        a.setRequirement(r);
        RecommendReturnSchema instance = new RecommendReturnSchema();
        instance.setPerson(p);
        instance.setApropiatenessScore(1.0);
        instance.setAvailabilityScore(1.0);
        instance.setRequirement(r);
        int expResult = -1;
        int result = instance.compareTo(a);
        assertEquals(expResult, result);
    }
    
}
