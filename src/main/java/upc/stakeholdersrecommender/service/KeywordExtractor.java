package upc.stakeholdersrecommender.service;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Keywords extractor functionality handler
 */
class KeywordExtractor {

    public double tf(List<String> doc, String term) {
            double result = 0;
            for (String word : doc) {
                if (term.equalsIgnoreCase(word))
                    result++;
            }
            return result / doc.size();
        }


        public double idf(List<List<String>> docs, String term) {
            double n = 0;
            for (List<String> doc : docs) {
                for (String word : doc) {
                    if (term.equalsIgnoreCase(word)) {
                        n++;
                        break;
                    }
                }
            }
            return Math.log(docs.size() / n);
        }


        public double tfIdf(List<String> doc, List<List<String>> docs, String term) {
            return tf(doc, term) * idf(docs, term);

        }

    private List<String> analyze(String text, Analyzer analyzer) throws IOException {
        List<String> result = new ArrayList<String>();
        TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(text));
        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while(tokenStream.incrementToken()) {
            result.add(attr.toString());
        }
        return result;
    }

    private void englishAnalyze(String text) throws IOException {
        List<String> result = analyze(text, new EnglishAnalyzer());
    }


    }
