package upc.stakeholdersrecommender.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import upc.stakeholdersrecommender.entity.Skill;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Requirement implements Serializable {

    private String id;

    private Integer effort;

    @JsonIgnore
    private List<Skill> skills = new ArrayList<Skill>();

    @JsonIgnore
    private Date modified;

    private String description;

    private String modified_at;

    public Requirement() {

    }

    public Requirement(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addSkill(Skill auxiliar) {
        this.skills.add(auxiliar);
    }

    public Integer getEffort() {
        return effort;
    }

    public void setEffort(Integer effort) {
        this.effort = effort;
    }

    public String getModified_at() {
        return modified_at;
    }

    public void setModified_at(String modified_at) {
        this.modified_at = modified_at;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
}
