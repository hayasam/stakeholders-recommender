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
public class PersonTest {
    


    @Test
    public void testGetUsername() {
        System.out.println("getUsername");
        Person instance = new Person("Nam");
        String expResult = "Nam";
        String result = instance.getUsername();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetUsername() {
        System.out.println("setUsername");
        String username = "";
        Person instance = new Person();
        instance.setUsername(username);
    }

    @Test
    public void testGetAvailability() {
        System.out.println("getAvailability");
        Person instance = new Person();
        Double expResult = null;
        Double result = instance.getAvailability();
        assertEquals(expResult, result);
    }


    @Test
    public void testSetAvailability() {
        System.out.println("setAvailability");
        Double availability = 1.0;
        Person instance = new Person();
        instance.setAvailability(availability);
        assertTrue(1.0==instance.getAvailability());
    }
    
}
