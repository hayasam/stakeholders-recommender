package upc.stakeholdersrecommender.domain.keywords;

import com.linguistic.rake.Rake;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import upc.stakeholdersrecommender.domain.Requirement;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class RAKEKeywordExtractor {

    static Double cutoff=6.0;

        static public List<Map<String,Double>> extractKeywords(List<String> corpus) throws IOException {
            List<Map<String,Double>> res= new ArrayList<>();
            Rake rake=new Rake();
            for (String s:corpus) {
                String text="";
                for (String k:RAKEanalyzeNoStopword(s)) {
                    text=text+" "+k;
                }
                Map<String,Double> aux=rake.getKeywordsFromText(text);
                String sum="";
                for (String j:aux.keySet()) {
                    Double val=aux.get(j);
                    if (val>=cutoff) sum=sum+" "+j;
                }
                List<String> result=RAKEanalyze(sum);
                Map<String,Double> helper=new HashMap<>();
                for (String i:result) {
                    helper.put(i,aux.get(i));
                }
                res.add(helper);
            }
            return res;
        }
    static public List<String> computeTFIDFSingular(Requirement req) throws IOException {
        Rake rake = new Rake();
        String text = "";
            for (String k : RAKEanalyzeNoStopword(req.getDescription())) {
                text = text + " " + k;
            }
            Map<String, Double> aux = rake.getKeywordsFromText(text);
            text="";
            for (String l:aux.keySet()) {
                Double val=aux.get(l);
                if (val>=cutoff); text=text+" "+l;
            }
            return RAKEanalyze(text);
        }


    static public Map<String, Map<String, Double>> computeTFIDFRake(Collection<Requirement> corpus) throws IOException {
        List<String> docs = new ArrayList<>();
        for (Requirement r : corpus) {
            docs.add(r.getDescription());
        }
        List<Map<String, Double>> res = RAKEKeywordExtractor.extractKeywords(docs);
        int counter = 0;
        return TFIDFKeywordExtractor.getStringMapMap(corpus, res, counter);
    }
    static List<String> RAKEanalyze(String text) throws IOException {
        Analyzer analyzer = CustomAnalyzer.builder()
                .withTokenizer("standard")
                .addTokenFilter("lowercase")
                .addTokenFilter("stop")
                .build();
        return analyze(text, analyzer);
    }
    static List<String> RAKEanalyzeNoStopword(String text) throws IOException {
        Analyzer analyzer = CustomAnalyzer.builder()
                .withTokenizer("standard")
                .addTokenFilter("lowercase")
                .addTokenFilter("porterstem")
                .build();
        return analyze(text, analyzer);
    }

    static List<String> analyze(String text, Analyzer analyzer) throws IOException {
        List<String> result = new ArrayList<>();
        TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(text));
        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            result.add(attr.toString());
        }
        return result;
    }

    static String clean_text(String text) {
        text = text.replaceAll("(\\{.*?})", " code ");
        text = text.replaceAll("[$,;\\\"/:|!?=()><_{}'[0-9]]", " ");
        return text;
    }

}
