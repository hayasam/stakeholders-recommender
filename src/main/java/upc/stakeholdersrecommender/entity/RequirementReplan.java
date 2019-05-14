package upc.stakeholdersrecommender.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "requirement_to_replan")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RequirementReplan implements Serializable {

    @EmbeddedId
    private RequirementReplanId id;

    private String idReplan;

    private String projectIdQuery;


    public RequirementReplan() {

    }

    public RequirementReplan(RequirementReplanId id) {
        this.id = id;
    }

    public RequirementReplanId getID() {
        return id;
    }

    public void setID(RequirementReplanId id) {
        this.id = id;
    }

    public String getID_Replan() {
        return idReplan;
    }

    public void setID_Replan(String idReplan) {
        this.idReplan = idReplan;
    }

    public RequirementReplanId getId() {
        return id;
    }

    public void setId(RequirementReplanId id) {
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
