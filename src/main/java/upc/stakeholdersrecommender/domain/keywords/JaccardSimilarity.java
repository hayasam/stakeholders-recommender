package upc.stakeholdersrecommender.domain.keywords;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.Math.min;
import static java.lang.StrictMath.sqrt;

public class JaccardSimilarity {

    static public double jaccardSimilarity(Set<String> a, Set<String> b) {

        Set<String> union = new HashSet<String>(a);
        union.addAll(b);

        Set<String> intersection = new HashSet<String>(a);
        intersection.retainAll(b);

        return (double) intersection.size() / min(union.size(),5);    }

    static public double cosine(List<Map<String, Double>> res, Integer a, Integer b) {
        Double cosine=0.0;
        Map<String,Double> wordsA=res.get(a);
        Map<String,Double> wordsB=res.get(b);
        Set<String> intersection= new HashSet<String>(wordsA.keySet());
        intersection.retainAll(wordsB.keySet());
        for (String s: intersection) {
            Double forA=wordsA.get(s);
            Double forB=wordsB.get(s);
            cosine+=forA*forB;
        }
        Double normA=norm(wordsA);
        Double normB=norm(wordsB);

        cosine=cosine/(normA*normB);
        return cosine;
    }

    private static Double norm(Map<String, Double> wordsB) {
        Double norm=0.0;
        for (String s:wordsB.keySet()) {
            norm+=wordsB.get(s)*wordsB.get(s);
        }
        Double result=sqrt(norm);
        return result;
    }

}


