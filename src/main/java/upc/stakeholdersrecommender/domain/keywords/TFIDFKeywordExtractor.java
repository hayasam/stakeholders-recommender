package upc.stakeholdersrecommender.domain.keywords;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import upc.stakeholdersrecommender.domain.Requirement;
import upc.stakeholdersrecommender.domain.Schemas.RequirementDocument;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import static java.lang.StrictMath.sqrt;
import static org.apache.commons.lang3.math.NumberUtils.isParsable;

public class TFIDFKeywordExtractor {

    Double cutoffParameter = 4.0; //This can be set to different values for different selectivity values
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


    private List<String> analyze(String text, Analyzer analyzer) throws IOException {
        List<String> result = new ArrayList<String>();
        text=clean_text(text);
        TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(text));
        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            result.add(attr.toString());
        }
        return result;
    }

    private List<String> englishAnalyze(String text) throws IOException {
        Analyzer analyzer = CustomAnalyzer.builder()
                .withTokenizer("standard")
                .addTokenFilter("lowercase")
                .addTokenFilter("porterstem")
                .addTokenFilter("stop")
                .build();
        return analyze(text, analyzer);
    }

    public Map<String,Map<String, Double>> computeTFIDF(Collection<Requirement> corpus) throws IOException {
        List<List<String>> docs = new ArrayList<List<String>>();
        for (Requirement r : corpus) {
            docs.add(englishAnalyze(r.getDescription()));
        }
        List<Map<String, Double>> res = tfIdf(docs);
        int counter=0;
        Map<String,Map<String, Double>> ret=new HashMap<String,Map<String, Double>>();
        for (Requirement r : corpus) {
            ret.put(r.getId(),res.get(counter));
            counter++;
        }
        return ret;

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
                if (tfidf >= cutoffParameter && s.length()>1) aux.put(s, tfidf);
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



    private Double norm(Map<String, Double> wordsB) {
        Double norm=0.0;
        for (String s:wordsB.keySet()) {
            norm+=wordsB.get(s)*wordsB.get(s);
        }
        Double result=sqrt(norm);
        return result;
    }

    private String clean_text(String text) {
        System.out.println(text);
        text = text.replaceAll("(\\{.*?})", " code ");
        text = text.replaceAll("[$,;\\\"/:|!?=%,()><_{}']", " ");
        text = text.replaceAll("] \\[", "][");
        String result = "";
        if (text.contains("[")) {
            String[] p=text.split("]\\[");
          /*  if (text.charAt(text.length()-1)!=']') {
                String[] tempor = text.split("]");
                p[p.length - 1] = tempor[tempor.length - 1].concat("]");
                result = " " + tempor[tempor.length - 1];
            }*/
            for (int iter=0;iter<p.length;++iter) {
                String f=p[iter];
                if (f.charAt(0)!='[') f="["+f;
                if (f.charAt(f.length()-1)!=']') f=f.concat("]");
                String[] thing = f.split("\\[");
                if (thing.length > 1) {
                    String[] help = thing[1].split("]");
                    if (help.length > 0) {
                        String[] badIdea = help[0].split(" ");
                        String nice = "";
                        for (String s : badIdea) {
                            nice=nice.concat(s);
                        }
                        for (int i = 0; i < 10; ++i) {
                            result = result.concat(" " + nice);
                        }
                    }
                }
            }
        }
        String[] aux4 = text.split("]");
        String[] aux2=aux4[aux4.length-1].split(" ");
        for (String a : aux2) {
            String helper = "";
            if (a.contains(".")) {
                String[] thing = a.split(".");
                if (thing.length > 2) {
                    helper = helper.concat(" " + a);
                } else {
                    for (String str : thing) {
                        if (isParsable(str)) {
                            helper = helper.concat(str);
                        } else helper = helper.concat(" " + str);
                    }
                }
            }

            else if (a.contains("-")) {
                String[] thing = a.split("-");
                if (thing.length > 2) {
                    helper = helper.concat(" " + a);
                } else {
                    for (String str : thing) {
                        if (isParsable(str)) {
                            helper = helper.concat(str);
                        } else helper = helper.concat(" " + str);
                    }
                }
            }

            if (helper.length() > 0 || isParsable(a)) {
                String[] aux3;
                if (helper.contains(".")) aux3 = helper.split(".");
                else if (helper.contains("-")) aux3 = helper.split("-");
                else {
                    aux3=new String[1];
                    aux3[0]=helper;
                }
                if (isParsable(a)) result = result.concat(a);
                else if (isParsable(aux3[0])) result = result.concat(helper);
                else result = result.concat(" " + helper);
            }
            else if (a.equals("for") || a.equals("to") || a.equals("in"));
            else {
                if (a.length() > 1) {
                    result = result.concat(" " + a);
                }

            }
        }
        System.out.println(result);
        return result;
    }

    public Map<String,Map<String, Double>> computeTFIDF(List<RequirementDocument> corpus) throws IOException {
        List<List<String>> docs = new ArrayList<List<String>>();
        for (RequirementDocument r : corpus) {
            docs.add(englishAnalyze(r.getDescription()));
        }
        List<Map<String, Double>> res = tfIdf(docs);
        int counter=0;
        Map<String,Map<String, Double>> ret=new HashMap<String,Map<String, Double>>();
        for (RequirementDocument r : corpus) {
            ret.put(r.getId(),res.get(counter));
            counter++;
        }
        return ret;

    }
}
