package upc.stakeholdersrecommender.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import upc.stakeholdersrecommender.domain.Requirement;

import javax.persistence.ElementCollection;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "requirementSR")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RequirementSR implements Serializable {

    @ElementCollection
    List<String> skills;
    @EmbeddedId
    private RequirementSRId id;
    private String projectIdQuery;

    public RequirementSR() {

    }

    public RequirementSR(RequirementSRId id) {
        this.id = id;
    }

    public RequirementSR(Requirement req, String id) {
        this.id = new RequirementSRId(id, req.getId());
        this.projectIdQuery = req.getId();
    }

    public RequirementSRId getID() {
        return id;
    }

    public void setID(RequirementSRId id) {
        this.id = id;
    }

    public RequirementSRId getId() {
        return id;
    }

    public void setId(RequirementSRId id) {
        this.id = id;
    }

    public String getProjectIdQuery() {
        return projectIdQuery;
    }

    public void setProjectIdQuery(String projectIdQuery) {
        this.projectIdQuery = projectIdQuery;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<String> skills) {
        this.skills = skills;
    }
}
