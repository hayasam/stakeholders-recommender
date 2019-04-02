package upc.stakeholdersrecommender.domain.keywords;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;
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


    public List<Map<String, Double>> extractKeywords(List<String> corpus) throws Exception {
        List<List<String>> docs = new ArrayList<List<String>>();
        for (String s : corpus) {
            docs.add(englishAnalyze(s));
        }
        List<List<String>> processed=preProcess(docs);
        List<Map<String, Double>> res = tfIdf(processed);
        return res;

    }

    private List<List<String>> preProcess(List<List<String>> corpus) throws Exception
    {
        InputStream modelIn = null;
        POSModel POSModel = null;
        List<List<String>> toRet=new ArrayList<>();
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


    private List<Map<String, Double>> tfIdf(List<List<String>> docs) {
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
                if (tfidf>=cutoffParameter) aux.put(s, tfidf);
            }
            tfidfComputed.add(aux);
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
                .addTokenFilter("stop")
                .addTokenFilter("commongrams")
                .addTokenFilter("englishminimalstem")
                .build();
        return analyze(text, analyzer);
    }

// Llamada al servicio 217.172.12.199:9404
}
