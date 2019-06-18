

package upc.stakeholdersrecommender.service;

import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import upc.stakeholdersrecommender.domain.Schemas.EffortCalculatorSchema;
import upc.stakeholdersrecommender.domain.Schemas.SetEffortSchema;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EffortCalculatorTest {
    @Autowired
    EffortCalculator instance;

    @Test
    public void testSetEffort() throws IOException {
        System.out.println("setEffort");
        ObjectMapper mapper = new ObjectMapper();
        SetEffortSchema req;
        String jsonInString = "{\n" +
                "  \"effortToHour\": [\n" +
                "    {\n" +
                "      \"effort\": \"1.0\",\n" +
                "      \"hours\": \"1.0\"\n" +
                "    },\n" +
                "        {\n" +
                "      \"effort\": \"2.0\",\n" +
                "      \"hours\": \"2.0\"\n" +
                "    },\n" +
                "        {\n" +
                "      \"effort\": \"3.0\",\n" +
                "      \"hours\": \"3.0\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        req = mapper.readValue(jsonInString, SetEffortSchema.class);
        instance.setEffort(req, "1","UPC");
    }

    @Test
    public void testEffortCalc() throws IOException {
        System.out.println("effortCalc");
        ObjectMapper mapper = new ObjectMapper();
        EffortCalculatorSchema req;
        String jsonInString = "{\n" +
                "  \"requirements\": [\n" +
                "    {\n" +
                "      \"effort\": \"1.0\",\n" +
                "      \"hours\": \"1.0\",\n" +
                "      \"id\": \"1\"\n" +
                "    },\n" +
                "        {\n" +
                "      \"effort\": \"1.0\",\n" +
                "      \"hours\": \"1.0\",\n" +
                "      \"id\": \"2\"\n" +
                "    },\n" +
                "        {\n" +
                "      \"effort\": \"1.0\",\n" +
                "      \"hours\": \"2.0\",\n" +
                "      \"id\": \"3\"\n" +
                "    },\n" +
                "        {\n" +
                "      \"effort\": \"1.0\",\n" +
                "      \"hours\": \"3.0\",\n" +
                "      \"id\": \"4\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        req = mapper.readValue(jsonInString, EffortCalculatorSchema.class);
        instance.effortCalc(req, "1","UPC");
    }

}
