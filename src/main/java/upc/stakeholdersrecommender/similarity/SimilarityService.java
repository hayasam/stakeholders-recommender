package upc.stakeholdersrecommender.similarity;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import upc.stakeholdersrecommender.domain.CorpusSchema;
import upc.stakeholdersrecommender.domain.Schemas.BERTResult;
import upc.stakeholdersrecommender.domain.Schemas.BERTSchema;
import upc.stakeholdersrecommender.domain.Schemas.ExtractTest;
import upc.stakeholdersrecommender.domain.Schemas.ResourceSkill;
import upc.stakeholdersrecommender.domain.SimilaritySchema;
import upc.stakeholdersrecommender.domain.keywords.TFIDFKeywordExtractor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class SimilarityService {

    TFIDFKeywordExtractor extr;
    SimilarityCalc jac=new SimilarityCalc();

  /*  @Autowired
    TextualSim sim;
    */
/*
    public void sif(CorpusSchema request) throws IOException {

        String[] columns = {"Text1","Text2","Similarity"};
        Workbook workbook = new XSSFWorkbook();
        CreationHelper createHelper = workbook.getCreationHelper();
        Sheet sheet = workbook.createSheet("Results");
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        Row headerRow = sheet.createRow(0);
        for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }
        int rowNum=0;

        List<List<String>> one=new ArrayList<List<String>>();
        List<List<String>>  two=new ArrayList<List<String>>();
        for (int i=0;i<request.getCorpus().size();++i) {
            SimilaritySchema sim=request.getCorpus().get(i);
            one.add(englishAnalyze(sim.getFrom()));
            two.add(englishAnalyze(sim.getTo()));
        }
        List<Double> result=sim.scores(one,two);
        for (int i=0;i<result.size();++i) {
            System.out.println("------------------------------");
            System.out.println("Similarity is "+result.get(i));
            System.out.println("For title : " + one.get(i));
            System.out.println("For title : " + two.get(i));
            System.out.println("------------------------------");
            Row row = sheet.createRow(rowNum++);
            row.createCell(0)
                    .setCellValue(request.getThing().get(i).getFrom());

            row.createCell(1)
                    .setCellValue(request.getThing().get(i).getTo());
            row.createCell(2).setCellValue(result.get(i));


        }
        FileOutputStream fileOut = new FileOutputStream("OutputWithSIF.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();

    }
    */

    public void buildModel(ExtractTest request) throws Exception {
        ////
        int rowNum=0;
        extr = new TFIDFKeywordExtractor();
        Map<String, Map<String, Double>> res = extr.extractKeywords(request.getCorpus());
      /*  List<Set<String>> toExamine=ExtractWords(res);
        for (int i=0;i<request.getCorpus().size();++i) {
            for (int j=i;j<request.getCorpus().size();++j) {
                if (i!=j) {
                   // Double jaccard = jaccardSimilarity(toExamine.get(i), toExamine.get(j));
                    Double cosine = cosine(res,i,j);
                    if (cosine*100 > 1) {
                        System.out.println("------------------------------");
                        System.out.println("Similarity between " + i + " and " + j + " is " + cosine);
                        String keysetA = toExamine.get(i).toString();
                        String keysetB = toExamine.get(j).toString();
                        System.out.println("For title : " + request.getCorpus().get(i));
                        System.out.println("For title : " + request.getCorpus().get(j));
                        System.out.println("Keywords of : " + keysetA);
                        System.out.println("Keywords of : " + keysetB);
                        System.out.println("------------------------------");
                    }
                }

            }
        }
        */
    }
    public void extract(CorpusSchema request) throws Exception {
        String[] columns = {"Text1","Text2","Similarity"};
        Workbook workbook = new XSSFWorkbook();
        CreationHelper createHelper = workbook.getCreationHelper();
        Sheet sheet = workbook.createSheet("Results");
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        Row headerRow = sheet.createRow(0);
        for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }
        int rowNum=0;
        for (int i=0;i<request.getThing().size();++i) {
            // Double jaccard = jaccardSimilarity(toExamine.get(i), toExamine.get(j));
            Double cosine = jac.cosine(extr.getModel(),request.getThing().get(i).getFrom(),request.getThing().get(i).getTo());
            Row row = sheet.createRow(rowNum++);
            row.createCell(0)
                    .setCellValue(request.getThing().get(i).getFrom());

            row.createCell(1)
                    .setCellValue(request.getThing().get(i).getTo());
            row.createCell(2).setCellValue(cosine);
/*
                        System.out.println("------------------------------");
                        System.out.println("Similarity between " + i +" is " + cosine);
                        System.out.println("For title : " + request.getThing().get(i).getFrom());
                        System.out.println("For title : " + request.getThing().get(i).getTo());
                        System.out.println("------------------------------");
                        */

        }
        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("Output.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();

    }


    private List<Set<String>> ExtractWords(Map<String, Map<String, Double>> res) {
        List<Set<String>> result= new ArrayList<Set<String>>();
        for (int i=0;i<res.size();++i) {
            Set<String> aux= res.get(i).keySet();
            result.add(aux);
        }
        return result;
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

    public void BERTExtract(CorpusSchema request) throws Exception {

        for (int i=0;i<request.getThing().size();++i) {
            SimilaritySchema sim=request.getThing().get(i);
            BERTSchema bert=new BERTSchema();
            bert.setId(100);
            List toPut=new ArrayList<String>();
            toPut.add(sim.getFrom());
            toPut.add(sim.getTo());
            bert.setTexts(toPut);
            bert.setIs_tokenized(false);
            BERTResult res=BERTPost(bert);
            System.out.println(res.getResults());

        }

    }

    public BERTResult BERTPost(BERTSchema bert) {
        RestTemplate restTemplate = new RestTemplate();
        BERTResult response=restTemplate.postForObject(
                "http://127.0.0.1:8125/encode",
                    bert, BERTResult.class);
        return response;
    }



}
