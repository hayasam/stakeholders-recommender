package upc.stakeholdersrecommender.domain.Preprocess;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import upc.stakeholdersrecommender.domain.Requirement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PreprocessService {
    public PreprocessService() {}

    public List<Requirement> preprocess(List<Requirement> requirements) {
        RequirementPreprocessList toSend=new RequirementPreprocessList();
        List<RequirementPreprocess> aux=new ArrayList<>();
        Map<String,Requirement> reqMap=new HashMap<>();
        for (Requirement r:requirements) {
            reqMap.put(r.getId(),r);
            RequirementPreprocess req=new RequirementPreprocess();
            req.setId(r.getId());
            req.setDescription(r.getDescription());
            req.setTitle(r.getName());
            aux.add(req);
        }
        toSend.setRequirements(aux);
        RestTemplate temp=new RestTemplate();
        RequirementPreprocessedList res= temp.postForObject("ur",toSend,RequirementPreprocessedList.class);
        List<Requirement> result=new ArrayList<>();
        for (RequirementPreprocessed r:res.getRequirements()) {
            Requirement re=reqMap.get(r.getId());
            re.setDescription(r.getDescription());
            re.setName("");
            result.add(re);
        }
        return result;
    }


}
