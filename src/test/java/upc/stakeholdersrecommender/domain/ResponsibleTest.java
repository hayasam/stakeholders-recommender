/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package upc.stakeholdersrecommender.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ResponsibleTest {
    

    @Test
    public void testGetRequirement() {
        System.out.println("getRequirement");
        Responsible instance = new Responsible();
        String expResult = "exp";
        instance.setRequirement("exp");
        String result = instance.getRequirement();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetRequirement() {
        System.out.println("setRequirement");
        String requirements = "r";
        Responsible instance = new Responsible();
        instance.setRequirement(requirements);
        assertEquals("r",instance.getRequirement());
    }


    @Test
    public void testGetPerson() {
        System.out.println("getPerson");
        Responsible instance = new Responsible();
        String expResult = "per";
        instance.setPerson("per");
        String result = instance.getPerson();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetPerson() {
        System.out.println("setPerson");
        String person = "p";
        Responsible instance = new Responsible();
        instance.setPerson(person);
        assertEquals("p",instance.getPerson());

    }
    
}
