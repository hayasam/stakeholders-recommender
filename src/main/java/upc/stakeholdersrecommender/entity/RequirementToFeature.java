package upc.stakeholdersrecommender.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "requirement_to_replan")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RequirementToFeature implements Serializable {

    @EmbeddedId
    private RequirementId id;

    private String idReplan;

    private String projectIdQuery;


    public RequirementToFeature() {

    }

    public RequirementToFeature(RequirementId id) {
        this.id = id;
    }

    public RequirementId getID() {
        return id;
    }

    public void setID(RequirementId id) {
        this.id = id;
    }

    public String getID_Replan() {
        return idReplan;
    }

    public void setID_Replan(String idReplan) {
        this.idReplan = idReplan;
    }

    public RequirementId getId() {
        return id;
    }

    public void setId(RequirementId id) {
        this.id = id;
    }

    public String getIdReplan() {
        return idReplan;
    }

    public void setIdReplan(String idReplan) {
        this.idReplan = idReplan;
    }

    public String getProjectIdQuery() {
        return projectIdQuery;
    }

    public void setProjectIdQuery(String projectIdQuery) {
        this.projectIdQuery = projectIdQuery;
    }

}
