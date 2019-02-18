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

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public Integer getCreated_at() { return created_at; }

    public void setCreated_at(Integer created_at) { this.created_at = created_at; }

}
