package upc.stakeholdersrecommender.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "personSR")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PersonSR implements Serializable {

    @EmbeddedId
    private PersonSRId id;

    private String projectIdQuery;

    private Double availability;

    private Integer hours;

    private String name;

    @ElementCollection
    @Embedded
    private List<Skill> skills;

    public PersonSR() {

    }

    public PersonSR(PersonSRId id, String projectIdQuer, Double availability) {
        this.id = id;
        this.projectIdQuery = projectIdQuer;
        this.availability = availability;
        this.name = id.getPersonId();
    }

    public PersonSR(PersonSRId id, String projectIdQuer, Double availability, List<Skill> skills) {
        this.id = id;
        this.projectIdQuery = projectIdQuer;
        this.availability = availability;
        this.name = id.getPersonId();
        this.skills = skills;
    }

    public PersonSR(PersonSRId id) {
        this.id = id;
        this.name = id.getPersonId();

    }

    public PersonSRId getId() {
        return id;
    }

    public void setId(PersonSRId id) {
        this.id = id;
    }

    public Double getAvailability() {
        return availability;
    }

    public void setAvailability(Double availability) {
        this.availability = availability;
    }

    public String getProjectIdQuery() {
        return projectIdQuery;
    }

    public void setProjectIdQuery(String projectId) {
        this.projectIdQuery = projectId;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }
}
