
package upc.stakeholdersrecommender.service;




import com.linguistic.rake.Rake;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RAKEKeywordExtractor {

    public List<Map<String,Double>> extractKeywords(List<String> corpus) {
        List<Map<String,Double>> res= new ArrayList<Map<String,Double>>();
        Rake rake=new Rake();
        for (String s:corpus) {
            res.add(rake.getKeywordsFromText(s));
        }
        return res;
    }
}


