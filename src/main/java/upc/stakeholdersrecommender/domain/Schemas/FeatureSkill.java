package upc.stakeholdersrecommender.domain.Schemas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FeatureSkill implements Serializable {

    List<RequiredSkill> required_skills;

    public List<RequiredSkill> getRequired_skills() {
        return required_skills;
    }

    public void setRequired_skills(List<RequiredSkill> required_skills) {
        this.required_skills = required_skills;
    }

    public List<String> getSkillIds() {
        List<String> result=new ArrayList<String>();
        for (RequiredSkill req:required_skills) {
            result.add(req.id);
        }
        return result;
    }

}
