

package upc.stakeholdersrecommender.service;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import upc.stakeholdersrecommender.domain.Schemas.BatchSchema;
import upc.stakeholdersrecommender.domain.Schemas.RecommendReturnSchema;
import upc.stakeholdersrecommender.domain.Schemas.RecommendSchema;


public class StakeholdersRecommenderServiceTest {
    
    public StakeholdersRecommenderServiceTest() {
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
/*
    @Test
    public void testRecommend() throws Exception {
        System.out.println("recommend");
        RecommendSchema request = null;
        int k = 0;
        Boolean projectSpecific = null;
        StakeholdersRecommenderService instance = new StakeholdersRecommenderService();
        List<RecommendReturnSchema> expResult = null;
        List<RecommendReturnSchema> result = instance.recommend(request, k, projectSpecific);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testRecommend_reject() {
        System.out.println("recommend_reject");
        String rejectedId = "";
        String userId = "";
        String requirementId = "";
        StakeholdersRecommenderService instance = new StakeholdersRecommenderService();
        instance.recommend_reject(rejectedId, userId, requirementId);
        fail("The test case is a prototype.");
    }

    @Test
    public void testAddBatch() throws Exception {
        System.out.println("addBatch");
        BatchSchema request = null;
        Boolean withAvailability = false;
        Boolean withComponent=null;
        StakeholdersRecommenderService instance = new StakeholdersRecommenderService();
        Integer expResult = null;
        Integer result = instance.addBatch(request, withAvailability,withComponent);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }
    */
}
