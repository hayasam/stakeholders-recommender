package upc.stakeholdersrecommender.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Requirement implements Serializable {

    private String id;

    @JsonIgnore
    private List<Skill> skills=new ArrayList<Skill>();

    private String name;

    private String description;

    private Integer created_at;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Integer created_at) {
        this.created_at = created_at;
    }

    public void addSkill(Skill auxiliar) {
        this.skills.add(auxiliar);
    }
}
