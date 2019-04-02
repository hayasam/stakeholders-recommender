package upc.stakeholdersrecommender.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.TreeSet;

// Currenty has no use
@Entity
@Table(name = "requirementSkills")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RequirementSkills implements Serializable {
    @Id
    private String requirementId;

    private String projectIdQuery;

    private TreeSet<Integer> skills;

    public RequirementSkills(String id) {
        requirementId = id;
    }

    public RequirementSkills() {
    }

    public RequirementSkills(String requirementId, String projectQuery) {
        this.requirementId = requirementId;
        this.projectIdQuery = projectQuery;
    }

    public String getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(String requirementId) {
        this.requirementId = requirementId;
    }

    public TreeSet<Integer> getSkills() {
        return skills;
    }

    public void setSkills(TreeSet<Integer> skills) {
        this.skills = skills;
    }

    public String getProjectIdQuery() {
        return projectIdQuery;
    }

    public void setProjectIdQuery(String projectIdQuery) {
        this.projectIdQuery = projectIdQuery;
    }
}
