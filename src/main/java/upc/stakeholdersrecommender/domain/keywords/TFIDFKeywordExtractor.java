package upc.stakeholdersrecommender.domain.keywords;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import upc.stakeholdersrecommender.domain.Schemas.CorpusSchema;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import static java.lang.StrictMath.sqrt;

public class TFIDFKeywordExtractor {

    Double cutoffParameter = 2.0;
    Map<String, Map<String, Double>> model;
    private Map<String, Integer> corpusFrequency = new HashMap<String, Integer>();

    public Map<String, Integer> getCorpusFrequency() {
        return corpusFrequency;
    }

    public void setCorpusFrequency(Map<String, Integer> corpusFrequency) {
        this.corpusFrequency = corpusFrequency;
    }

    public Double getCutoffParameter() {
        return cutoffParameter;
    }

    public void setCutoffParameter(Double cutoffParameter) {
        this.cutoffParameter = cutoffParameter;
    }

    public Map<String, Map<String, Double>> getModel() {
        return model;
    }

    public void setModel(Map<String, Map<String, Double>> model) {
        this.model = model;
    }

    /*
    public Map<String, Map<String, Double>> extractKeywords(List<String> corpus) throws Exception {
        List<List<String>> docs = new ArrayList<List<String>>();
        for (String s : corpus) {
            docs.add(englishAnalyze(s));
        }
        Map<String, Map<String, Double>> res = tfIdf(docs, corpus);
        model=res;
        return res;

    }
*/

    private Map<String, Integer> tf(List<String> doc) {
        Map<String, Integer> frequency = new HashMap<String, Integer>();
        for (String s : doc) {
            if (frequency.containsKey(s)) frequency.put(s, frequency.get(s) + 1);
            else {
                frequency.put(s, 1);
                if (corpusFrequency.containsKey(s)) corpusFrequency.put(s, corpusFrequency.get(s) + 1);
                else corpusFrequency.put(s, 1);
            }

        }
        return frequency;
    }


    private double idf(Integer size, Integer frequency) {
        return Math.log(size / frequency + 1);
    }

    /*
        private Map<String,Map<String, Double>> tfIdf(List<List<String>> docs, List<String> corpus) {
            Map<String,Map<String, Double>> tfidfComputed = new HashMap<String,Map<String, Double>>();
            List<Map<String, Integer>> wordBag = new ArrayList<Map<String, Integer>>();
            for (List<String> doc : docs) {
                wordBag.add(tf(doc));
            }
            Integer i = 0;
            for (List<String> doc : docs) {
                HashMap<String, Double> aux = new HashMap<String, Double>();
                for (String s : doc) {
                    Double idf = idf(docs.size(), corpusFrequency.get(s));
                    Integer tf = wordBag.get(i).get(s);
                    Double tfidf = idf * tf;
                    if (tfidf>=cutoffParameter) aux.put(s, tfidf);
                }
                tfidfComputed.put(corpus.get(i),aux);
                ++i;
            }
            return tfidfComputed;

        }
    */
    private List<String> analyze(String text, Analyzer analyzer) throws IOException {
        List<String> result = new ArrayList<String>();
        TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(text));
        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            result.add(attr.toString());
        }
        Integer i = 0;
        return result;
    }

    private List<String> englishAnalyze(String text) throws IOException {
        Analyzer analyzer = CustomAnalyzer.builder()
                .withTokenizer("standard")
                .addTokenFilter("lowercase")
                .addTokenFilter("commongrams")
                .addTokenFilter("porterstem")
                .addTokenFilter("stop")
                .build();
        return analyze(text, analyzer);
    }

    /*
        private List<String> englishLematize(String text) throws IOException,Exception {
            StanfordLemmatizer stan= new StanfordLemmatizer();
            List<String> result=stan.lemmatize(text);
            return result;
        }
    */
    public List<Map<String, Double>> computeTFIDF(List<String> corpus) throws Exception {
        List<List<String>> docs = new ArrayList<List<String>>();
        for (String s : corpus) {
            docs.add(englishAnalyze(s));
        }
        List<Map<String, Double>> res = tfIdf(docs, corpus);
        return res;

    }

    private List<Map<String, Double>> tfIdf(List<List<String>> docs, List<String> corpus) {
        List<Map<String, Double>> tfidfComputed = new ArrayList<Map<String, Double>>();
        List<Map<String, Integer>> wordBag = new ArrayList<Map<String, Integer>>();
        for (List<String> doc : docs) {
            wordBag.add(tf(doc));
        }
        Integer i = 0;
        for (List<String> doc : docs) {
            HashMap<String, Double> aux = new HashMap<String, Double>();
            for (String s : doc) {
                Double idf = idf(docs.size(), corpusFrequency.get(s));
                Integer tf = wordBag.get(i).get(s);
                Double tfidf = idf * tf;
                if (tfidf >= cutoffParameter) aux.put(s, tfidf);
            }
            tfidfComputed.add(aux);
            ++i;
        }
        return tfidfComputed;

    }

    public double cosineSimilarity(Map<String,Double> wordsA,Map<String,Double> wordsB) {
        Double cosine=0.0;
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

    public void extr(CorpusSchema request) throws Exception {
        Map<String,Map<String,Integer>> keywordGraph= new HashMap<String,Map<String,Integer>>();
        List<Map<String, Double>> res = computeTFIDF(request.getCorpus());
        Map<String,Integer> amount=new HashMap<String,Integer>();
        for (Map<String,Double> words:res) {
            for (String word: words.keySet()) {
                Map<String,Integer> keyword=new HashMap<String,Integer>();
                if (keywordGraph.containsKey(word)) {
                    keyword=keywordGraph.get(word);
                    amount.put(word,amount.get(word)+keyword.keySet().size());
                }
                else  {
                    keyword=new HashMap<String,Integer>();
                    amount.put(word,keyword.keySet().size());

                }
                for (String adjacentTo: words.keySet()) {
                    if (keyword.containsKey(adjacentTo)) {
                        keyword.put(adjacentTo,keyword.get(adjacentTo)+1);
                    }
                    else {
                        keyword.put(adjacentTo,1);
                    }
                }
                keywordGraph.put(word,keyword);
            }
        }
        Map<String,Map<String,Double>> keywordValue= new HashMap<String,Map<String,Double>>();
//
        for (String s:keywordGraph.keySet()) {
            Map<String,Double> aux= new HashMap<String,Double>();
            Map<String,Integer> line=keywordGraph.get(s);
            for (String key:line.keySet()) {
                aux.put(s,line.get(key).doubleValue()/amount.get(s).doubleValue());
            }
        }

    }

    private Double norm(Map<String, Double> wordsB) {
        Double norm=0.0;
        for (String s:wordsB.keySet()) {
            norm+=wordsB.get(s)*wordsB.get(s);
        }
        Double result=sqrt(norm);
        return result;
    }
}
