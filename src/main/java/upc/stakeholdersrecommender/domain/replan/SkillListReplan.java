package upc.stakeholdersrecommender.domain.replan;

import java.io.Serializable;

public class SkillListReplan implements Serializable {

    private Integer skill_id;
    private Double weight;

    public SkillListReplan() {

    }

    public SkillListReplan(SkillReplan skill) {
        this.weight = skill.getWeight();
        this.skill_id = skill.getId();
    }


    public SkillListReplan(Integer skill_id, Double weight) {
        this.skill_id = skill_id;
        this.weight = weight;
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

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
