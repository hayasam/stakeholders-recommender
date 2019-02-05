package upc.stakeholdersrecommender.domain.replan;

import java.io.Serializable;

public class SkillListReplan implements Serializable {

    private Integer skill_id;

    public SkillListReplan() {

    }

    public SkillListReplan(Integer skill_id) {
        this.skill_id = skill_id;
    }

    public Integer getSkill_id() {
        return skill_id;
    }

    public void setSkill_id(Integer skill_id) {
        this.skill_id = skill_id;
    }
}
