package upc.stakeholdersrecommender.domain.Preprocess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import upc.stakeholdersrecommender.domain.Requirement;
import upc.stakeholdersrecommender.domain.Schemas.RecommendSchema;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

@Service
public class PreprocessService {

    Logger logger = Logger.getLogger(PreprocessService.class.getName());

    public PreprocessService() {
    }

    public List<Requirement> preprocess(List<Requirement> requirements,Integer test) throws IOException {
        List<Requirement> result = new ArrayList<>();
        if (test==0) {
            RequirementPreprocessList toSend = new RequirementPreprocessList();
            List<RequirementPreprocess> aux = new ArrayList<>();
            Map<String, Requirement> reqMap = new HashMap<>();
            for (Requirement r : requirements) {
                reqMap.put(r.getId(), r);
                RequirementPreprocess req = new RequirementPreprocess();
                req.setId(r.getId());
                req.setDescription(r.getDescription());
                if (r.getName() != null) req.setTitle(r.getName());
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
        }
        else if (test==1) {
            ObjectMapper map = new ObjectMapper();
            File file = new File("src/main/resources/testingFiles/PreprocessingResponse.txt");
            String jsonInString = null;
            jsonInString = FileUtils.readFileToString(file, StandardCharsets.US_ASCII);
        }
        else if (test==2) {
            ObjectMapper map = new ObjectMapper();
            File file = new File("src/main/resources/testingFiles/PreprocessBig.txt");
            String jsonInString = null;
            jsonInString = FileUtils.readFileToString(file, StandardCharsets.US_ASCII);
            result = new ArrayList<>(Arrays.asList(map.readValue(jsonInString, Requirement[].class)));
        }
        return result;
    }


    public List<String> preprocessSingular(Requirement requirement,Integer test) throws IOException {
        RequirementPreprocessList toSend = new RequirementPreprocessList();
        List<RequirementPreprocess> aux = new ArrayList<>();
        RequirementPreprocess req = new RequirementPreprocess();
        req.setId(requirement.getId());
        req.setDescription(requirement.getDescription());
        if (requirement.getName() != null) req.setTitle(requirement.getName());
        else req.setTitle("");
        aux.add(req);
        toSend.setRequirements(aux);
        RestTemplate temp = new RestTemplate();
        List<String> result = new ArrayList<>();
        if (test==0 && test==1) {
            RequirementPreprocessedList res = temp.postForObject("http://217.172.12.199:9406/keywords-extraction/requirements?stemmer=false", toSend, RequirementPreprocessedList.class);
            List<RequirementPreprocessed> re = res.getRequirements();
            RequirementPreprocessed processed = re.get(0);
            for (String j : processed.getDescription().split(" ")) {
                if (!j.equals(""))
                    result.add(j);
            }
        }
        else {
            ObjectMapper map=new ObjectMapper();
                String[] s=map.readValue("[\"indicate\",\"completed\",\"requirements\",\"releases\",\"status\",\"of\",\"requirements\",\"and\",\"releases\",\"visualization\",\"of\",\"completed\",\"requirements\",\"releases\",\"indicate\",\"completed\",\"requirements\",\"releases\",\"status\",\"of\",\"requirements\",\"and\",\"releases\"]\n",
                        String[].class);
                result=(Arrays.asList(s));
        }
        return result;
    }

}
