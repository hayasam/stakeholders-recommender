/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package upc.stakeholdersrecommender.entity;

import java.util.HashSet;
import java.util.Map;
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
public class RejectedPersonTest {
    
    @Test
    public void testGetUser() {
        System.out.println("getUser");
        RejectedPerson instance = new RejectedPerson();
        RejectedPersonId expResult = null;
        RejectedPersonId result = instance.getUser();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetUser() {
        System.out.println("setUser");
        RejectedPersonId user = new RejectedPersonId("one","two");
        RejectedPerson instance = new RejectedPerson();
        instance.setUser(user);
        assertTrue(instance.getUser().getPersonId().equals("one")&&instance.getUser().getOrganizationId().equals("two"));
    }

    @Test
    public void testGetDeleted() {
        System.out.println("getDeleted");
        RejectedPerson instance = new RejectedPerson();
        Map<String, HashSet<String>> expResult = null;
        Map<String, HashSet<String>> result = instance.getDeleted();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetDeleted() {
        System.out.println("setDeleted");
        Map<String, HashSet<String>> deleted = null;
        RejectedPerson instance = new RejectedPerson();
        instance.setDeleted(deleted);
        assertEquals(null,instance.getDeleted());
    }
    
}
