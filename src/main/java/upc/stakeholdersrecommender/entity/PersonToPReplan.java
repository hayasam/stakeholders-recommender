package upc.stakeholdersrecommender.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "person_to_replan")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PersonToPReplan implements Serializable {

    @EmbeddedId
    private PersonId id;

    private String projectIdQuery;

    private String idReplan;

    private Double availability;

    private Integer hours;

    private String name;

    public PersonToPReplan() {

    }

    public PersonToPReplan(PersonId id, String projectIdQuer, String idReplan, Double availability) {
        this.id = id;
        this.projectIdQuery = projectIdQuer;
        this.idReplan = idReplan;
        this.availability = availability;
        this.name=id.getPersonId();
    }

    public PersonToPReplan(PersonId id) {
        this.id = id;
        this.idReplan = id.getprojectId();
        this.name=id.getPersonId();

    }

    public PersonId getId() {
        return id;
    }

    public void setId(PersonId id) {
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

    public String getIdReplan() {
        return idReplan;
    }

    public void setIdReplan(String id_replan) {
        this.idReplan = id_replan;
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
}
