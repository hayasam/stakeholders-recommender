package upc.stakeholdersrecommender.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "requirement_to_replan")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RequirementToFeature {

    @Id
    private String id;

    private Integer idReplan;

    private Integer projectIdQuery;


    public RequirementToFeature() {

    }

    public RequirementToFeature(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }

    public void setID_Replan(Integer idReplan) {
        this.idReplan = idReplan;
    }

    public Integer getID_Replan() {
        return idReplan;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
