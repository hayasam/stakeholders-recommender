

package upc.stakeholdersrecommender.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import upc.stakeholdersrecommender.domain.keywords.TFIDFKeywordExtractor;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TfIdfTest {


    @Test
    public void computeTFIDFTest() throws IOException {
        TFIDFKeywordExtractor keywordExtractor=new TFIDFKeywordExtractor();
        String object="";
        ObjectMapper map=new ObjectMapper();
        Requirement[] recs=map.readValue(object, Requirement[].class);
        List<Requirement> reqList=new ArrayList<>();
        for (Requirement r:recs) {
            reqList.add(r);
        }
        Map<String, Map<String,Double>> res=keywordExtractor.computeTFIDF(reqList);
        assertEquals("",map.writeValueAsString(res));

    }

    @Test
    public void computeTFIDFSingular() throws IOException {
        TFIDFKeywordExtractor keywordExtractor=new TFIDFKeywordExtractor();
        String object="";
        ObjectMapper map=new ObjectMapper();
        TypeReference<HashMap<String, Integer>> typeRef
                = new TypeReference<HashMap<String, Integer>>() {};
        Map<String,Integer> recs=map.readValue(object, typeRef);
        Requirement req=new Requirement();
        //Assign values to req
        List<String> res=keywordExtractor.computeTFIDFSingular(req,recs,100);
        assertEquals("",map.writeValueAsString(res));

    }


}
