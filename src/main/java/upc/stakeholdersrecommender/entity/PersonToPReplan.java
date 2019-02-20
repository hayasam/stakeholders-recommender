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

    private Integer idReplan;

    public PersonToPReplan() {

    }

    public PersonId getId() {
        return id;
    }

    public PersonToPReplan(PersonId id) {
        this.id=id;
        idReplan=id.getprojectId();
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

    public Integer getIdReplan() {
        return idReplan;
    }

    public void setIdReplan(Integer id_replan) {
        this.idReplan = id_replan;
    }
}
