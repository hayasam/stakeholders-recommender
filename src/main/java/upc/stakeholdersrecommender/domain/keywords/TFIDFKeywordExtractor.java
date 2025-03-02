package upc.stakeholdersrecommender.domain.keywords;

import edu.stanford.nlp.util.ArraySet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import upc.stakeholdersrecommender.domain.Requirement;
import upc.stakeholdersrecommender.domain.TextPreprocessing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class TFIDFKeywordExtractor {

    private Double cutoffParameter = 4.0; //This can be set to different values for different selectivity (more or less keywords)
    private HashMap<String, Integer> corpusFrequency = new HashMap<>();
    private TextPreprocessing text_preprocess=new TextPreprocessing();

    private Map<String, Integer> tf(List<String> doc) {
        Map<String, Integer> frequency = new HashMap<>();
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
        return Math.log(size.doubleValue() / frequency.doubleValue() + 1.0);
    }


    private List<String> analyze(String text, Analyzer analyzer) throws IOException {
        List<String> result = new ArrayList<>();
        text = clean_text(text);
        return RAKEKeywordExtractor.getAnalyzedStrings(text, analyzer, result);
    }

    private List<String> englishAnalyze(String text) throws IOException {
        Analyzer analyzer = CustomAnalyzer.builder()
                .withTokenizer("standard")
                .addTokenFilter("lowercase")
                .addTokenFilter("stop")
                .addTokenFilter("kstem")
                .build();
        return analyze(text, analyzer);
    }

    public Map<String, Map<String, Double>> computeTFIDF(Collection<Requirement> corpus) throws IOException {
        List<List<String>> docs = new ArrayList<>();
        for (Requirement r : corpus) {
            docs.add(englishAnalyze(r.getDescription()));
        }
        List<Map<String, Double>> res = tfIdf(docs);
        int counter = 0;
        return getStringMapMap(corpus, res, counter);

    }


    static Map<String, Map<String, Double>> getStringMapMap(Collection<Requirement> corpus, List<Map<String, Double>> res, int counter) {
        Map<String, Map<String, Double>> ret = new HashMap<>();
        for (Requirement r : corpus) {
            ret.put(r.getId(), res.get(counter));
            counter++;
        }
        return ret;
    }

    public List<String> computeTFIDFSingular(Requirement req, Map<String, Integer> model, Integer corpusSize) throws IOException {
        List<String> doc = englishAnalyze(clean_text(req.getDescription()));
        Map<String, Integer> wordBag = tf(doc);
        List<String> keywords = new ArrayList<>();
        for (String s : wordBag.keySet()) {
            if (model.containsKey(s)) {
                model.put(s, model.get(s) + 1);
                if (wordBag.get(s) * idf(corpusSize, model.get(s)) >= cutoffParameter) keywords.add(s);
            } else {
                model.put(s, 1);
                if (wordBag.get(s) * idf(corpusSize, model.get(s)) >= cutoffParameter) keywords.add(s);
            }
        }
        return keywords;
    }

    public Map<String, Map<String, Double>> computeTFIDFExtra(Map<String, Integer> model, Integer size, Map<String, Requirement> trueRecs) throws IOException {
        Map<String, Map<String, Double>> result = new HashMap<>();
        for (String l : trueRecs.keySet()) {
            Requirement req = trueRecs.get(l);
            List<String> doc = englishAnalyze(clean_text(req.getDescription()));
            Map<String, Integer> wordBag = tf(doc);
            Map<String, Double> keywords = new HashMap<>();
            for (String s : wordBag.keySet()) {
                if (model.containsKey(s)) {
                    model.put(s, model.get(s) + 1);
                    if (wordBag.get(s) * idf(size, model.get(s)) >= cutoffParameter) keywords.put(s, 0.0);
                } else {
                    model.put(s, 1);
                    if (wordBag.get(s) * idf(size, model.get(s)) >= cutoffParameter) keywords.put(s, 0.0);
                }
            }
            result.put(l, keywords);
        }
        return result;

    }


    private List<Map<String, Double>> tfIdf(List<List<String>> docs) {
        List<Map<String, Double>> tfidfComputed = new ArrayList<>();
        List<Map<String, Integer>> wordBag = new ArrayList<>();
        for (List<String> doc : docs) {
            wordBag.add(tf(doc));
        }
        int i = 0;
        for (List<String> doc : docs) {
            HashMap<String, Double> aux = new HashMap<>();
            for (String s : doc) {
                Double idf = idf(docs.size(), corpusFrequency.get(s));
                Integer tf = wordBag.get(i).get(s);
                Double tfidf = idf * tf;
                if (tfidf >= cutoffParameter && s.length() > 1) aux.put(s, tfidf);
            }
            tfidfComputed.add(aux);
            ++i;
        }
        return tfidfComputed;

    }

    private String clean_text(String text) throws IOException {
        text= text_preprocess.text_preprocess(text);
        String result = "";
        if (text.contains("[")) {
            String[] p = text.split("]\\[");
            for (String f : p) {
                if (f.charAt(0) != '[') f = "[" + f;
                if (f.charAt(f.length() - 1) != ']') f = f.concat("]");
                String[] thing = f.split("\\[");
                if (thing.length > 1) {
                    String[] help = thing[1].split("]");
                    if (help.length > 0) {
                        String[] badIdea = help[0].split(" ");
                        String nice = "";
                        for (String s : badIdea) {
                            nice = nice.concat(s);
                        }
                        for (int i = 0; i < 10; ++i) {
                            result = result.concat(" " + nice);
                        }
                    }
                }
            }
        }
        String[] aux4 = text.split("]");
        String[] aux2 = aux4[aux4.length - 1].split(" ");
        for (String a : aux2) {
            String helper = "";
            if (a.toUpperCase().equals(a)) {
                for (int i = 0; i < 10; ++i) {
                    helper = helper.concat(" " + a);
                }
                a = helper;
            }
            result = result.concat(" " + a);
        }
        return result;
    }


    public HashMap<String, Integer> getCorpusFrequency() {
        return corpusFrequency;
    }

    public void setCorpusFrequency(HashMap<String, Integer> corpusFrequency) {
        this.corpusFrequency = corpusFrequency;
    }

    public Double getCutoffParameter() {
        return cutoffParameter;
    }

    public void setCutoffParameter(Double cutoffParameter) {
        this.cutoffParameter = cutoffParameter;
    }



}
