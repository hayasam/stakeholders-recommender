package upc.stakeholdersrecommender.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

public class Person implements Serializable {

    private String username;

    private String email;

    private List<Skill> skills;

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

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public void setEmail(String email) {this.email=email;}

    public String getEmail(){ return email; }
}
