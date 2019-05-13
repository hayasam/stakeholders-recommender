package upc.stakeholdersrecommender.domain.replan;

import java.io.Serializable;
import java.util.List;

public class ArraySkillListReplan implements Serializable {

    List<SkillReplan> skills;

    public List<SkillReplan> getSkillReplan() {
        return skills;
    }

    public void setSkillReplan(List<SkillReplan> skillListReplan) {
        skills = skillListReplan;
    }
}
