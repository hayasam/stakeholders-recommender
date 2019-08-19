package upc.stakeholdersrecommender.domain.Preprocess;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import upc.stakeholdersrecommender.domain.Requirement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PreprocessService {
    public PreprocessService() {
    }

    public List<Requirement> preprocess(List<Requirement> requirements) {
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
        List<Requirement> result = new ArrayList<>();
        for (RequirementPreprocessed r : res.getRequirements()) {
            Requirement re = reqMap.get(r.getId());
            re.setDescription(r.getDescription());
            re.setName("");
            result.add(re);
        }
        return result;
    }


    public List<String> preprocessSingular(Requirement requirement) {
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
        RequirementPreprocessedList res = temp.postForObject("http://217.172.12.199:9406/keywords-extraction/requirements?stemmer=false", toSend, RequirementPreprocessedList.class);
        List<String> result = new ArrayList<>();
        List<RequirementPreprocessed> re = res.getRequirements();
        RequirementPreprocessed processed = re.get(0);
        for (String j : processed.getDescription().split(" ")) {
            if (!j.equals(""))
                result.add(j);
        }
        return result;
    }

}
