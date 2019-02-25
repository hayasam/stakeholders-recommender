package upc.stakeholdersrecommender.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "requirement_to_replan")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RequirementToFeature {

    @EmbeddedId
    private RequirementId id;

    private Integer idReplan;

    private Integer projectIdQuery;


    public RequirementToFeature() {

    }

    public RequirementToFeature(RequirementId id) {
        this.id = id;
    }

    public RequirementId getID() {
        return id;
    }

    public void setID_Replan(Integer idReplan) {
        this.idReplan = idReplan;
    }

    public Integer getID_Replan() {
        return idReplan;
    }

    public void setID(RequirementId id) {
        this.id = id;
    }

    public RequirementId getId() {
        return id;
    }

    public void setId(RequirementId id) {
        this.id = id;
    }

    public Integer getIdReplan() {
        return idReplan;
    }

    public void setIdReplan(Integer idReplan) {
        this.idReplan = idReplan;
    }

    public Integer getProjectIdQuery() {
        return projectIdQuery;
    }

    public void setProjectIdQuery(Integer projectIdQuery) {
        this.projectIdQuery = projectIdQuery;
    }

}
