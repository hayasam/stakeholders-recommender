package upc.stakeholdersrecommender.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import upc.stakeholdersrecommender.domain.Schemas.BatchSchema;
import upc.stakeholdersrecommender.domain.Schemas.ProjectKeywordSchema;
import upc.stakeholdersrecommender.domain.Schemas.RecommendReturnSchema;
import upc.stakeholdersrecommender.domain.Schemas.RecommendSchema;
import upc.stakeholdersrecommender.entity.Skill;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StakeholdersRecommenderServiceTest {
    @Autowired
    StakeholdersRecommenderService instance;

    @Test
    public void testRecommend() throws Exception {
        System.out.println("recommend");
        ObjectMapper mapper = new ObjectMapper();
        RecommendSchema req = new RecommendSchema();
        testAddBatch();
        String jsonInString = "{\n" +
                "  \"project\": {\n" +
                "    \"id\": \"1\"\n" +
                "  },\n" +
                "  \"requirement\": {\n" +
                "    \"description\": \"This is not really a requirement, but an example\",\n" +
                "    \"effort\": \"3.0\",\n" +
                "    \"id\": \"1\",\n" +
                "    \"modified_at\": \"2014-01-13T15:14:17Z\",\n" +
                "    \"name\": \"This is a title\",\n" +
                "    \"requirementParts\": [\n" +
                "      {\n" +
                "        \"id\": \"3\",\n" +
                "        \"name\": \"UI\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"user\": {\n" +
                "    \"username\": \"John Doe\"\n" +
                "  }\n" +
                "}";
        req = mapper.readValue(jsonInString, RecommendSchema.class);

        int k = 10;
        List<RecommendReturnSchema> result = instance.recommend(req, k, true, "UPC");
        String res = mapper.writeValueAsString(result);
        assertEquals(res, "[{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"230\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.16666666666666666}]");
    }

    @Test
    public void testNotProjectSpecific() throws Exception {
        System.out.println("recommend");
        ObjectMapper mapper = new ObjectMapper();
        RecommendSchema req;
        testAddBatch();
        String jsonInString = "{\n" +
                "  \"project\": {\n" +
                "    \"id\": \"1\"\n" +
                "  },\n" +
                "  \"requirement\": {\n" +
                "    \"description\": \"This is not really a requirement, but an example\",\n" +
                "    \"effort\": \"3.0\",\n" +
                "    \"id\": \"1\",\n" +
                "    \"modified_at\": \"2014-01-13T15:14:17Z\",\n" +
                "    \"name\": \"This is a title\",\n" +
                "    \"requirementParts\": [\n" +
                "      {\n" +
                "        \"id\": \"3\",\n" +
                "        \"name\": \"UI\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"user\": {\n" +
                "    \"username\": \"John Doe\"\n" +
                "  }\n" +
                "}";
        req = mapper.readValue(jsonInString, RecommendSchema.class);

        int k = 10;
        List<RecommendReturnSchema> result = instance.recommend(req, k, false, "UPC");
        String res = mapper.writeValueAsString(result);
        assertEquals(res, "[{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"230\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.16666666666666666}]");
    }

    @Test
    public void testRecommendAvailability() throws Exception {
        System.out.println("recommend");
        ObjectMapper mapper = new ObjectMapper();
        RecommendSchema req;
        testAddBatchAvailabilityAutoMappingComponent();
        String jsonInString = "{\n" +
                "  \"project\": {\n" +
                "    \"id\": \"1\"\n" +
                "  },\n" +
                "  \"requirement\": {\n" +
                "    \"description\": \"This is not really a requirement, but an example\",\n" +
                "    \"effort\": \"3.0\",\n" +
                "    \"id\": \"1\",\n" +
                "    \"modified_at\": \"2014-01-13T15:14:17Z\",\n" +
                "    \"name\": \"This is a title\",\n" +
                "    \"requirementParts\": [\n" +
                "      {\n" +
                "        \"id\": \"3\",\n" +
                "        \"name\": \"UI\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"user\": {\n" +
                "    \"username\": \"John Doe\"\n" +
                "  }\n" +
                "}";
        req = mapper.readValue(jsonInString, RecommendSchema.class);

        int k = 10;
        List<RecommendReturnSchema> result = instance.recommend(req, k, true, "UPC");
        String res = mapper.writeValueAsString(result);
        assertEquals(res, "[{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"230\"},\"availabilityScore\":0.97,\"appropiatenessScore\":0.16666666666666666}]");
    }


    @Test
    public void testRecommend_reject() throws Exception {
        System.out.println("recommend_reject");
        testAddBatch();
        instance.recommend_reject("230", "230", "1", "UPC");
        ObjectMapper mapper = new ObjectMapper();
        RecommendSchema req;
        String jsonInString = "{\n" +
                "  \"project\": {\n" +
                "    \"id\": \"1\"\n" +
                "  },\n" +
                "  \"requirement\": {\n" +
                "    \"description\": \"This is not really a requirement, but an example\",\n" +
                "    \"effort\": \"3.0\",\n" +
                "    \"id\": \"1\",\n" +
                "    \"modified_at\": \"2014-01-13T15:14:17Z\",\n" +
                "    \"name\": \"This is a title\",\n" +
                "    \"requirementParts\": [\n" +
                "      {\n" +
                "        \"id\": \"3\",\n" +
                "        \"name\": \"UI\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"user\": {\n" +
                "    \"username\": \"230\"\n" +
                "  }\n" +
                "}";
        req = mapper.readValue(jsonInString, RecommendSchema.class);

        int k = 10;
        List<RecommendReturnSchema> result = instance.recommend(req, k, true, "UPC");
        String res = mapper.writeValueAsString(result);
        assertEquals(res, "[]");
    }
    @Test
    public void testUndo_Recommend_reject() throws Exception {
        System.out.println("recommend_reject");
        testAddBatch();
        testRecommend_reject();
        instance.undo_recommend_reject("230", "230", "1", "UPC");
        ObjectMapper mapper = new ObjectMapper();
        RecommendSchema req;
        String jsonInString = "{\n" +
                "  \"project\": {\n" +
                "    \"id\": \"1\"\n" +
                "  },\n" +
                "  \"requirement\": {\n" +
                "    \"description\": \"This is not really a requirement, but an example\",\n" +
                "    \"effort\": \"3.0\",\n" +
                "    \"id\": \"1\",\n" +
                "    \"modified_at\": \"2014-01-13T15:14:17Z\",\n" +
                "    \"name\": \"This is a title\",\n" +
                "    \"requirementParts\": [\n" +
                "      {\n" +
                "        \"id\": \"3\",\n" +
                "        \"name\": \"UI\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"user\": {\n" +
                "    \"username\": \"230\"\n" +
                "  }\n" +
                "}";
        req = mapper.readValue(jsonInString, RecommendSchema.class);

        int k = 10;
        List<RecommendReturnSchema> result = instance.recommend(req, k, true, "UPC");
        String res = mapper.writeValueAsString(result);
        assertEquals("[{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"230\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.16666666666666666}]", res);
    }

    @Test
    public void testGetSkills() throws Exception {
        System.out.println("recommend_reject");
        testAddBatch();
        ObjectMapper mapper=new ObjectMapper();
        int k = 10;
        List<Skill> result = instance.getPersonSkills("230","UPC",k);
        String res = mapper.writeValueAsString(result);
        assertEquals("[{\"name\":\"requir\",\"weight\":0.5},{\"name\":\"realli\",\"weight\":0.5}]", res);
    }



    @Test
    public void testRecommend_rejectTwice() throws Exception {
        System.out.println("recommend_reject");
        testAddBatch();
        instance.recommend_reject("Dummy", "230", "1", "UPC");
        instance.recommend_reject("230", "230", "1", "UPC");
        ObjectMapper mapper = new ObjectMapper();
        RecommendSchema req;
        String jsonInString = "{\n" +
                "  \"project\": {\n" +
                "    \"id\": \"1\"\n" +
                "  },\n" +
                "  \"requirement\": {\n" +
                "    \"description\": \"This is not really a requirement, but an example\",\n" +
                "    \"effort\": \"3.0\",\n" +
                "    \"id\": \"1\",\n" +
                "    \"modified_at\": \"2014-01-13T15:14:17Z\",\n" +
                "    \"name\": \"This is a title\",\n" +
                "    \"requirementParts\": [\n" +
                "      {\n" +
                "        \"id\": \"3\",\n" +
                "        \"name\": \"UI\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"user\": {\n" +
                "    \"username\": \"230\"\n" +
                "  }\n" +
                "}";
        req = mapper.readValue(jsonInString, RecommendSchema.class);

        int k = 10;
        List<RecommendReturnSchema> result = instance.recommend(req, k, true, "UPC");
        String res = mapper.writeValueAsString(result);
        assertEquals(res, "[]");
    }


    @Test
    public void testAddBatch() throws Exception {
        System.out.println("addBatch");
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "{\n" +
                "    \"projects\": [\n" +
                "        {\n" +
                "            \"id\": \"1\",\n" +
                "            \"specifiedRequirements\": [\"1\"]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"persons\": [\n" +
                "        {\n" +
                "            \"username\": \"230\"\n" +
                "        }\n" +
                "        ],\n" +
                "    \"responsibles\": [\n" +
                "        {\n" +
                "            \"requirement\": \"1\",\n" +
                "            \"person\": \"230\"\n" +
                "        }\n" +
                "        ],\n" +
                "    \"participants\": [\n" +
                "    \t        {\n" +
                "            \"project\": \"1\",\n" +
                "            \"person\": \"230\",\n" +
                "            \"availability\": \"100\"\n" +
                "        }\n" +
                "    \t],\n" +
                "  \"requirements\": [{\n" +
                "    \"description\": \"This is not really a requirement, but an example\",\n" +
                "    \"effort\": \"3.0\",\n" +
                "    \"id\": \"1\",\n" +
                "    \"modified_at\": \"2014-01-13T15:14:17Z\",\n" +
                "    \"name\": \"This is a title\",\n" +
                "    \"requirementParts\": [\n" +
                "      {\n" +
                "        \"id\": \"3\",\n" +
                "        \"name\": \"UI\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "  ]\n" +
                "    \t\n" +
                "}\n" +
                "    \t";
        BatchSchema bat = mapper.readValue(jsonInString, BatchSchema.class);
        String organization = "UPC";
        Integer result = instance.addBatch(bat, false, false, organization, false, false, false);
        Integer expected = 5;
        assertEquals(result, expected);
    }

    @Test
    public void testAddBatchAvailabilityAutoMapping() throws Exception {
        System.out.println("addBatch");
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "{\n" +
                "    \"projects\": [\n" +
                "        {\n" +
                "            \"id\": \"1\",\n" +
                "            \"specifiedRequirements\": [\"1\"]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"persons\": [\n" +
                "        {\n" +
                "            \"username\": \"230\"\n" +
                "        }\n" +
                "        ],\n" +
                "    \"responsibles\": [\n" +
                "        {\n" +
                "            \"requirement\": \"1\",\n" +
                "            \"person\": \"230\"\n" +
                "        }\n" +
                "        ],\n" +
                "    \"participants\": [\n" +
                "    \t        {\n" +
                "            \"project\": \"1\",\n" +
                "            \"person\": \"230\",\n" +
                "            \"availability\": \"100\"\n" +
                "        }\n" +
                "    \t],\n" +
                "  \"requirements\": [{\n" +
                "    \"description\": \"This is not really a requirement, but an example\",\n" +
                "    \"effort\": \"3.0\",\n" +
                "    \"id\": \"1\",\n" +
                "    \"modified_at\": \"2014-01-13T15:14:17Z\",\n" +
                "    \"name\": \"This is a title\",\n" +
                "    \"requirementParts\": [\n" +
                "      {\n" +
                "        \"id\": \"3\",\n" +
                "        \"name\": \"UI\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "  ]\n" +
                "    \t\n" +
                "}\n" +
                "    \t";
        BatchSchema bat = mapper.readValue(jsonInString, BatchSchema.class);
        String organization = "UPC";
        Integer result = instance.addBatch(bat, true, false, organization, true, false, false);
        Integer expected = 5;
        assertEquals(result, expected);
    }

    @Test
    public void testAddBatchAvailabilityAutoMappingComponent() throws Exception {
        System.out.println("addBatch");
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "{\n" +
                "    \"projects\": [\n" +
                "        {\n" +
                "            \"id\": \"1\",\n" +
                "            \"specifiedRequirements\": [\"1\"]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"persons\": [\n" +
                "        {\n" +
                "            \"username\": \"230\"\n" +
                "        }\n" +
                "        ],\n" +
                "    \"responsibles\": [\n" +
                "        {\n" +
                "            \"requirement\": \"1\",\n" +
                "            \"person\": \"230\"\n" +
                "        }\n" +
                "        ],\n" +
                "    \"participants\": [\n" +
                "    \t        {\n" +
                "            \"project\": \"1\",\n" +
                "            \"person\": \"230\",\n" +
                "            \"availability\": \"100\"\n" +
                "        }\n" +
                "    \t],\n" +
                "  \"requirements\": [{\n" +
                "    \"description\": \"This is not really a requirement, but an example\",\n" +
                "    \"effort\": \"3.0\",\n" +
                "    \"id\": \"1\",\n" +
                "    \"modified_at\": \"2014-01-13T15:14:17Z\",\n" +
                "    \"name\": \"This is a title\",\n" +
                "    \"requirementParts\": [\n" +
                "      {\n" +
                "        \"id\": \"3\",\n" +
                "        \"name\": \"UI\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "  ]\n" +
                "    \t\n" +
                "}\n" +
                "    \t";
        BatchSchema bat = mapper.readValue(jsonInString, BatchSchema.class);
        String organization = "UPC";
        Integer result = instance.addBatch(bat, true, true, organization, true, false, false);
        Integer expected = 5;
        assertEquals(result, expected);
    }
/*
    @Test
    public void testAddBatchLogging() throws Exception {
        System.out.println("addBatch");
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("src/main/resources/testingFiles/BatchTestLogging.txt");
        String jsonInString= FileUtils.readFileToString(file, StandardCharsets.US_ASCII);
        BatchSchema bat = mapper.readValue(jsonInString, BatchSchema.class);
        String organization = "UPC";
        Integer result = instance.addBatch(bat, true, true, organization, true, false, true);
        Integer expected = 22213;
        assertEquals(result, expected);
    }

*/

    @Test
    public void testAddBatchAvailabilityPreprocessing() throws Exception {
        System.out.println("addBatch");
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("src/main/resources/testingFiles/BatchTest.txt");
        String jsonInString= FileUtils.readFileToString(file, StandardCharsets.US_ASCII);
        BatchSchema bat = mapper.readValue(jsonInString, BatchSchema.class);
        String organization = "UPC";
        Integer result = instance.addBatch(bat, true, true, organization, true, true, true);
        Integer expected = 22213;
        assertEquals(result, expected);
    }

    @Test
    public void testAddBatchAvailabilityTfIdfLogging() throws Exception {
        System.out.println("addBatch");
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("src/main/resources/testingFiles/BatchTest.txt");
        String jsonInString= FileUtils.readFileToString(file, StandardCharsets.US_ASCII);
        BatchSchema bat = mapper.readValue(jsonInString, BatchSchema.class);
        String organization = "UPC";
        Integer result = instance.addBatch(bat, true, true, organization, true, false, true);
        Integer expected = 22213;
        assertEquals(result, expected);
    }

    @Test
    public void testRecommendTfIdf() throws Exception {
        System.out.println("recommend");
        ObjectMapper mapper = new ObjectMapper();
        RecommendSchema req;
        testAddBatchAvailabilityTfIdfLogging();
        File file = new File("src/main/resources/testingFiles/RecommendTest.txt");
        String jsonInString= FileUtils.readFileToString(file, StandardCharsets.US_ASCII);
        req = mapper.readValue(jsonInString, RecommendSchema.class);

        int k = 10;
        List<RecommendReturnSchema> result = instance.recommend(req, k, true, "UPC");
        String res = mapper.writeValueAsString(result);
        assertEquals("[{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"22\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.3570260322431573},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"1\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.3550470059206973},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"79\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.3514576147797954},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"2\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.3232583367637267},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"4\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.32297072990057346},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"105\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.3188856870084062},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"15\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.316735509859933},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"149\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.3160488625119256},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"229\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.28274576111845523},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"198\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.2182744148434397}]",res);
    }

    @Test
    public void testRecommendPreprocessingLogging() throws Exception {
        System.out.println("recommend");
        ObjectMapper mapper = new ObjectMapper();
        RecommendSchema req;
        testAddBatchAvailabilityPreprocessing();
        File file = new File("src/main/resources/testingFiles/RecommendTest.txt");
        String jsonInString= FileUtils.readFileToString(file, StandardCharsets.US_ASCII);
        req = mapper.readValue(jsonInString, RecommendSchema.class);

        int k = 10;
        List<RecommendReturnSchema> result = instance.recommend(req, k, true, "UPC");
        String res = mapper.writeValueAsString(result);
        assertEquals("[{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"114\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.3521163869028451},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"113\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.349448148083429},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"232\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.3317735038997425},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"233\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.3279027783878485},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"111\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.32637802991726417},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"234\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.3179875007991653},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"110\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.30398483580310104},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"112\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.3009058003298778},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"231\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.2912088808908845},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"230\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.28769962766034657}]",res);
    }

    @Test
    public void testRecommendLeftovers() throws Exception {
        System.out.println("recommend");
        ObjectMapper mapper = new ObjectMapper();
        RecommendSchema req;
        File file = new File("src/main/resources/testingFiles/BatchTestLeftover.txt");
        String jsonInString= FileUtils.readFileToString(file, StandardCharsets.US_ASCII);
        BatchSchema bat = mapper.readValue(jsonInString, BatchSchema.class);
        String organization = "UPC";
        instance.addBatch(bat, true, true, organization, true, true, false);
        file = new File("src/main/resources/testingFiles/RecommendTest.txt");
        jsonInString= FileUtils.readFileToString(file, StandardCharsets.US_ASCII);
        req = mapper.readValue(jsonInString, RecommendSchema.class);
        int k = 10;
        List<RecommendReturnSchema> result = instance.recommend(req, k, true, "UPC");
        String res = mapper.writeValueAsString(result);
        assertEquals("[{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"114\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.3521163869028451},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"113\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.349448148083429},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"232\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.3317735038997425},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"233\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.3279027783878485},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"111\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.32637802991726417},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"234\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.3179875007991653},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"110\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.30398483580310104},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"112\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.3009058003298778},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"231\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.2912088808908845},{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"235\"},\"availabilityScore\":1.0,\"appropiatenessScore\":0.2856033272577937}]",res);
    }


    @Test
    public void testRecommendPreprocessing() throws Exception {
        System.out.println("recommend");
        ObjectMapper mapper = new ObjectMapper();
        RecommendSchema req;
        testAddBatchAvailabilityAutoMappingComponent();
        File file = new File("src/main/resources/testingFiles/RecommendTest.txt");
        String jsonInString= FileUtils.readFileToString(file, StandardCharsets.US_ASCII);
        req = mapper.readValue(jsonInString, RecommendSchema.class);

        int k = 10;
        List<RecommendReturnSchema> result = instance.recommend(req, k, true, "UPC");
        String res = mapper.writeValueAsString(result);
        assertEquals("[{\"requirement\":{\"id\":\"1\"},\"person\":{\"username\":\"230\"},\"availabilityScore\":0.97,\"appropiatenessScore\":0.13636363636363635}]",res);
    }

    @Test
    public void testKeyword() throws Exception {
        System.out.println("addBatch");
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "{\n" +
                "    \"projects\": [\n" +
                "        {\n" +
                "            \"id\": \"1\",\n" +
                "            \"specifiedRequirements\": [\"1\"]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"persons\": [\n" +
                "        {\n" +
                "            \"username\": \"230\"\n" +
                "        }\n" +
                "        ],\n" +
                "    \"responsibles\": [\n" +
                "        {\n" +
                "            \"requirement\": \"1\",\n" +
                "            \"person\": \"230\"\n" +
                "        }\n" +
                "        ],\n" +
                "    \"participants\": [\n" +
                "    \t        {\n" +
                "            \"project\": \"1\",\n" +
                "            \"person\": \"230\",\n" +
                "            \"availability\": \"100\"\n" +
                "        }\n" +
                "    \t],\n" +
                "  \"requirements\": [{\n" +
                "    \"description\": \"This is not really a requirement, but an example\",\n" +
                "    \"effort\": \"3.0\",\n" +
                "    \"id\": \"1\",\n" +
                "    \"modified_at\": \"2014-01-13T15:14:17Z\",\n" +
                "    \"name\": \"This is a title\",\n" +
                "    \"requirementParts\": [\n" +
                "      {\n" +
                "        \"id\": \"3\",\n" +
                "        \"name\": \"UI\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "  ]\n" +
                "    \t\n" +
                "}\n" +
                "    \t";
        BatchSchema bat = mapper.readValue(jsonInString, BatchSchema.class);
        String organization = "UPC";
        Integer result = instance.addBatch(bat, true, true, organization, true, false, false);
        Integer expected = 5;
        assertEquals(result, expected);
        List<ProjectKeywordSchema> res = instance.extractKeywords("UPC", bat);
        assertTrue(res.get(0).getProjectId().equals("1"));
    }


}
