
package upc.stakeholdersrecommender.domain.keywords;

import java.util.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import upc.stakeholdersrecommender.domain.Requirement;

public class TFIDFKeywordExtractorTest {
    
    public TFIDFKeywordExtractorTest() {
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
    /*

    @Test
    public void testComputeTFIDF() throws Exception {
        System.out.println("computeTFIDF");
        Requirement single=new Requirement();
        single.setId("Id");
        single.setDescription("This is a tfidf description");
        List<Requirement> list=new ArrayList<>();
        list.add(single);
        Collection<Requirement> corpus = list;
        TFIDFKeywordExtractor instance = new TFIDFKeywordExtractor();
        Map<String, Map<String, Double>> expResult = null;
        Map<String, Map<String, Double>> result = instance.computeTFIDF(corpus);
        System.out.println(result);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testComputeTFIDFSingular() throws Exception {
        System.out.println("computeTFIDFSingular");
        Requirement single=new Requirement();
        single.setId("Id");
        single.setDescription("This is a tfidf description");
        Map<String, Integer> model = new HashMap<String,Integer>();
        TFIDFKeywordExtractor instance = new TFIDFKeywordExtractor();
        List<String> expResult = null;
        List<String> result = instance.computeTFIDFSingular(single, model);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetCorpusFrequency() {
        System.out.println("getCorpusFrequency");
        HashMap<String, Integer> corpusFrequency = new HashMap<>();
        corpusFrequency.put("Test",1);
        TFIDFKeywordExtractor instance = new TFIDFKeywordExtractor();
        instance.setCorpusFrequency(corpusFrequency);
        HashMap<String, Integer> expResult = corpusFrequency;
        HashMap<String, Integer> result = instance.getCorpusFrequency();
        assertEquals(expResult, result);
    }
    @Test
    public void testSetCorpusFrequency() {
        System.out.println("setCorpusFrequency");
        HashMap<String, Integer> corpusFrequency = new HashMap<>();
        corpusFrequency.put("Test",1);
        TFIDFKeywordExtractor instance = new TFIDFKeywordExtractor();
        instance.setCorpusFrequency(corpusFrequency);
    }


    @Test
    public void testGetCutoffParameter() {
        System.out.println("getCutoffParameter");
        TFIDFKeywordExtractor instance = new TFIDFKeywordExtractor();
        instance.setCutoffParameter(1.0);
        Double expResult = 1.0;
        Double result = instance.getCutoffParameter();
        assertEquals(expResult, result);
    }


    @Test
    public void testSetCutoffParameter() {
        System.out.println("setCutoffParameter");
        TFIDFKeywordExtractor instance = new TFIDFKeywordExtractor();
        instance.setCutoffParameter(1.0);
        Double expResult = 1.0;
        Double result = instance.getCutoffParameter();
        assertEquals(expResult, result);
    }
    */
}
