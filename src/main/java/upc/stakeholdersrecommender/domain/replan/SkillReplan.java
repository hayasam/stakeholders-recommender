package upc.stakeholdersrecommender.domain.replan;

import upc.stakeholdersrecommender.entity.Skill;

import java.io.Serializable;

public class SkillReplan implements Serializable {

    private Integer id;

    private String name;

    public SkillReplan() {

    }

    public SkillReplan(Skill s) {
        this.name = s.getName();
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
}
