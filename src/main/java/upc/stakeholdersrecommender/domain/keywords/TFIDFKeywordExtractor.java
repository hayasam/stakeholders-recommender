package upc.stakeholdersrecommender.domain.keywords;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TFIDFKeywordExtractor {

    private Map<String, Integer> corpusFrequency = new HashMap<String, Integer>();
    Double cutoffParameter=1.0;
    Map<String, Map<String, Double>> model;

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

    public Map<String, Map<String, Double>> extractKeywords(List<String> corpus) throws Exception {
        List<List<String>> docs = new ArrayList<List<String>>();
        for (String s : corpus) {
            docs.add(englishAnalyze(s));
        }
        List<List<String>> processed=preProcess(docs);
        Map<String, Map<String, Double>> res = tfIdf(processed, corpus);
        model=res;
        return res;

    }

    public Map<String,Double> extractKeywordsWithModel(String a) throws Exception {
        return model.get(a);
    }


    private List<List<String>> preProcess(List<List<String>> corpus) throws Exception
    {
        return corpus;
        /*
        InputStream modelIn = null;
        POSModel POSModel = null;
        List<List<String>> toRet=new ArrayList<>();
        */
        /*
        try{
            File f = new File("E:\\Trabajo\\stakeholders-recommender\\src\\main\\java\\upc\\stakeholdersrecommender\\domain\\keywords\\en-pos-maxent.bin");
            modelIn = new FileInputStream(f);
            POSModel = new POSModel(modelIn);
            POSTaggerME tagger = new POSTaggerME(POSModel);
            for (List<String> list:corpus) {
                String[] toTag=new String[list.size()];
                toTag=list.toArray(toTag);
                String[] tagged = tagger.tag(toTag);
                List<String> result=new ArrayList<String>();
                for (int i = 0; i < tagged.length; i++) {
                    if (tagged[i].equalsIgnoreCase("nn") || tagged[i].equalsIgnoreCase("vb")) {
                        result.add(list.get(i));
                    }
                }
                toRet.add(result);
            }
        return toRet;
        }
        catch(IOException e){
            throw new Exception("File fucked up");
        }
        */
    }

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
        return Math.log(size / frequency+1);
    }


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
               // .addTokenFilter("commongrams")
                .addTokenFilter("englishminimalstem")
               // .addTokenFilter("stop")
                .build();
        return analyze(text, analyzer);
    }

}
