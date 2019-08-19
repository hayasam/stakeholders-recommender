package upc.stakeholdersrecommender.entity;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SkillTest {

    @Test
    public void testGetName() {
        System.out.println("getName");
        Skill instance = new Skill();
        instance.setName("nam");
        String expResult = "nam";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetName() {
        System.out.println("setName");
        String name = "nam";
        Skill instance = new Skill();
        instance.setName(name);
        assertEquals("nam", instance.getName());
    }

    @Test
    public void testGetWeight() {
        System.out.println("getWeight");
        Skill instance = new Skill();
        Double expResult = null;
        Double result = instance.getWeight();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetWeight() {
        System.out.println("setWeight");
        Double weight = 1.0;
        Skill instance = new Skill();
        instance.setWeight(weight);
        assertTrue(1.0 == instance.getWeight());
    }

}
