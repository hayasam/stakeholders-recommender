package upc.stakeholdersrecommender.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "person_to_replan")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class PersonToPReplan {

    @EmbeddedId
    private PersonId id;

    private Integer projectIdQuery;

    private Integer id_replan;

    public PersonToPReplan() {

    }

    public PersonId getId() {
        return id;
    }

    public PersonToPReplan(PersonId id) {
        this.id=id;
        id_replan=id.getprojectId();
    }

    public void setId(PersonId id) {
        this.id = id;
    }

    public Integer getProjectIdQuery() {
        return projectIdQuery;
    }

    public void setProjectIdQuery(Integer projectId) {
        this.projectIdQuery = projectId;
    }

    public Integer getId_replan() {
        return id_replan;
    }

    public void setId_replan(Integer id_replan) {
        this.id_replan = id_replan;
    }
}
