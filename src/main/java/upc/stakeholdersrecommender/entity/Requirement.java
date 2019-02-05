package upc.stakeholdersrecommender.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "requirements")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Requirement implements Serializable {

    @Id
    private String id;

    @ManyToMany(
            cascade = CascadeType.ALL
    )
    private List<Skill> skills;

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
}
