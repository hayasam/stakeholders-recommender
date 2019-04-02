package upc.stakeholdersrecommender.domain.keywords;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JaccardSimilarity {

    static public double jaccardSimilarity(Set<String> a, Set<String> b) {

        Set<String> union = new HashSet<String>(a);
        union.addAll(b);

        Set<String> intersection = new HashSet<String>(a);
        intersection.retainAll(b);

        return (double) intersection.size() / union.size();    }
}


