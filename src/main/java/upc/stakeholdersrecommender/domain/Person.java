package upc.stakeholdersrecommender.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import upc.stakeholdersrecommender.domain.replan.SkillListReplan;
import upc.stakeholdersrecommender.domain.replan.SkillReplan;

import java.io.Serializable;
import java.util.List;

public class Person implements Serializable {

    private String username;

    private String email;

    private Double availability;

    @JsonIgnore
    private List<SkillListReplan> skills;

    public Person() {

    }

    public Person(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<SkillListReplan> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillListReplan> skills) {
        this.skills = skills;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getAvailability() {
        return availability;
    }

    public void setAvailability(Double availability) {
        this.availability = availability;
    }
}
