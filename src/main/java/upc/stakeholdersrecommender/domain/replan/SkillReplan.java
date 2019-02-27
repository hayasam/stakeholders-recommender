package upc.stakeholdersrecommender.domain.replan;

import upc.stakeholdersrecommender.domain.Skill;

import java.io.Serializable;

public class SkillReplan implements Serializable {

    private Integer id;

    private String name;

    private Double weight;

    public SkillReplan() {

    }

    public SkillReplan(Skill s) {
        this.name = s.getName();
        this.weight=s.getWeight();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getWeight() { return weight; }

    public void setWeight(Double weight) { this.weight = weight; }
}
