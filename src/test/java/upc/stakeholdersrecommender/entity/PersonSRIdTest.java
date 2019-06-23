/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package upc.stakeholdersrecommender.entity;

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
public class PersonSRIdTest {
    

    @Test
    public void testGetprojectId() {
        System.out.println("getprojectId");
        PersonSRId instance = new PersonSRId();
        instance.setProjectId("1");
        String expResult = "1";
        String result = instance.getprojectId();
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
    public void testHashCode() {
        System.out.println("hashCode");
        PersonSRId instance = new PersonSRId();
        int expResult =new PersonSRId().hashCode();
        int result = instance.hashCode();
        assertEquals(expResult, result);
    }
    
}
