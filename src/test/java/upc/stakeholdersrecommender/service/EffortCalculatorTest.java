

package upc.stakeholdersrecommender.service;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import upc.stakeholdersrecommender.domain.Schemas.EffortCalculatorSchema;
import upc.stakeholdersrecommender.domain.Schemas.SetEffortSchema;


public class EffortCalculatorTest {
    
    public EffortCalculatorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // Go lazy, create simple JSON batches for each method, and then copy the output
    @Test
    public void testEffortCalc() {
        System.out.println("effortCalc");
        EffortCalculatorSchema eff = null;
        String id = "";
        EffortCalculator instance = new EffortCalculator();
        instance.effortCalc(eff, id);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    // Go lazy, create simple JSON batches for each method, and then copy the output
    @Test
    public void testSetEffort() {
        System.out.println("setEffort");
        SetEffortSchema set = null;
        String id = "";
        EffortCalculator instance = new EffortCalculator();
        instance.setEffort(set, id);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
