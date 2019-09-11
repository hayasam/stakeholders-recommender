package upc.stakeholdersrecommender.domain.Preprocess;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.omg.CORBA.Object;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import upc.stakeholdersrecommender.domain.Requirement;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

@Service
public class PreprocessService {

    Logger logger = Logger.getLogger(PreprocessService.class.getName());

    public PreprocessService() {
    }

    public List<Requirement> preprocess(List<Requirement> requirements, Integer test) throws IOException {
        List<Requirement> result = new ArrayList<>();
        if (test == 0) {
            RequirementPreprocessList toSend = new RequirementPreprocessList();
            List<RequirementPreprocess> aux = new ArrayList<>();
            Map<String, Requirement> reqMap = new HashMap<>();
            for (Requirement r : requirements) {
                reqMap.put(r.getId(), r);
                RequirementPreprocess req = new RequirementPreprocess();
                req.setId(r.getId());
                String text = r.getDescription().replaceAll("[\\[\\].,:;!?\"&]", " ");
                req.setDescription(text);
                if (r.getName() != null) {
                    text = r.getName().replaceAll("[\\[\\].,:;!?\"&]", " ");
                    req.setTitle(text);
                }
                else req.setTitle("");
                aux.add(req);
            }
            toSend.setRequirements(aux);
            RestTemplate temp = new RestTemplate();
            RequirementPreprocessedList res = temp.postForObject("http://217.172.12.199:9406/keywords-extraction/requirements?stemmer=false", toSend, RequirementPreprocessedList.class);
            for (RequirementPreprocessed r : res.getRequirements()) {
                Requirement re = reqMap.get(r.getId());
                re.setDescription(r.getDescription());
                re.setName("");
                result.add(re);
            }
        } else if (test == 1) {
            ObjectMapper map = new ObjectMapper();
            File file = new File("src/main/resources/testingFiles/PreprocessingResponse.txt");
            String jsonInString = FileUtils.readFileToString(file, StandardCharsets.US_ASCII);
            result = new ArrayList<>(Arrays.asList(map.readValue(jsonInString, Requirement[].class)));
        } else if (test == 2) {
            ObjectMapper map = new ObjectMapper();
            File file = new File("src/main/resources/testingFiles/PreprocessBig.txt");
            String jsonInString = FileUtils.readFileToString(file, StandardCharsets.US_ASCII);
            result = new ArrayList<>(Arrays.asList(map.readValue(jsonInString, Requirement[].class)));
        }
        ObjectMapper map=new ObjectMapper();
        System.out.println(map.writeValueAsString(result));
        return result;
    }


    public List<String> preprocessSingular(Requirement requirement, Integer test) throws IOException {
        RequirementPreprocessList toSend = new RequirementPreprocessList();
        List<RequirementPreprocess> aux = new ArrayList<>();
        RequirementPreprocess req = new RequirementPreprocess();
        req.setId(requirement.getId());
        String text = requirement.getDescription().replaceAll("[\\[\\].,:;!?\"&]", " ");
        req.setDescription(text);
        if (requirement.getName() != null) {
            text = requirement.getName().replaceAll("[\\[\\].,:;!?\"&]", " ");
            req.setTitle(text);
        }
        else req.setTitle("");
        aux.add(req);
        toSend.setRequirements(aux);
        RestTemplate temp = new RestTemplate();
        List<String> result = new ArrayList<>();
        if (test == 0 && test == 1) {
            RequirementPreprocessedList res = temp.postForObject("http://217.172.12.199:9406/keywords-extraction/requirements?stemmer=false", toSend, RequirementPreprocessedList.class);
            List<RequirementPreprocessed> re = res.getRequirements();
            RequirementPreprocessed processed = re.get(0);
            for (String j : processed.getDescription().split(" ")) {
                if (!j.equals(""))
                    result.add(j);
            }
        } else {
            ObjectMapper map = new ObjectMapper();
            String[] s = map.readValue("[\"indicate\",\"completed\",\"requirements\",\"releases\",\"status\",\"of\",\"requirements\",\"and\",\"releases\",\"visualization\",\"of\",\"completed\",\"requirements\",\"releases\",\"indicate\",\"completed\",\"requirements\",\"releases\",\"status\",\"of\",\"requirements\",\"and\",\"releases\"]\n",
                    String[].class);
            result = (Arrays.asList(s));
        }

        ObjectMapper map=new ObjectMapper();
        System.out.println(map.writeValueAsString(result));
        return result;
    }

}
