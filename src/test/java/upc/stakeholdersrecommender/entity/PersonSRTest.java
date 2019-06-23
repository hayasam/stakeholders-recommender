/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package upc.stakeholdersrecommender.entity;

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
public class PersonSRTest {
    

    @Test
    public void testGetId() {
        System.out.println("getId");
        PersonSR instance = new PersonSR();
        PersonSRId expResult = null;
        PersonSRId result = instance.getId();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetId() {
        System.out.println("setId");
        PersonSRId id = null;
        PersonSR instance = new PersonSR();
        instance.setId(id);
    }


    @Test
    public void testGetAvailability() {
        System.out.println("getAvailability");
        PersonSR instance = new PersonSR();
        Double expResult = null;
        Double result = instance.getAvailability();
        assertEquals(expResult, result);
    }


    @Test
    public void testSetAvailability() {
        System.out.println("setAvailability");
        Double availability = null;
        PersonSR instance = new PersonSR();
        instance.setAvailability(availability);
    }

    @Test
    public void testGetProjectIdQuery() {
        System.out.println("getProjectIdQuery");
        PersonSR instance = new PersonSR();
        instance.setProjectIdQuery("id");
        String expResult = "id";
        String result = instance.getProjectIdQuery();
        assertEquals(expResult, result);
    }


    @Test
    public void testSetProjectIdQuery() {
        System.out.println("setProjectIdQuery");
        String projectId = "";
        PersonSR instance = new PersonSR();
        instance.setProjectIdQuery(projectId);
    }

    @Test
    public void testGetHours() {
        System.out.println("getHours");
        PersonSR instance = new PersonSR();
        Integer expResult = null;
        Double result = instance.getHours();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetHours() {
        System.out.println("setHours");
        Double hours = null;
        PersonSR instance = new PersonSR();
        instance.setHours(hours);
    }

    @Test
    public void testGetName() {
        System.out.println("getName");
        PersonSR instance = new PersonSR();
        instance.setName("nam");
        String expResult = "nam";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetName() {
        System.out.println("setName");
        String name = "";
        PersonSR instance = new PersonSR();
        instance.setName(name);
    }

    @Test
    public void testGetSkills() {
        System.out.println("getSkills");
        PersonSR instance = new PersonSR();
        List<Skill> expResult = null;
        List<Skill> result = instance.getSkills();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetSkills() {
        System.out.println("setSkills");
        List<Skill> skills = null;
        PersonSR instance = new PersonSR();
        instance.setSkills(skills);
    }


    @Test
    public void testGetComponents() {
        System.out.println("getComponents");
        PersonSR instance = new PersonSR();
        List<Skill> expResult = null;
        List<Skill> result = instance.getComponents();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetComponents() {
        System.out.println("setComponents");
        List<Skill> components = null;
        PersonSR instance = new PersonSR();
        instance.setComponents(components);
    }
    
}
