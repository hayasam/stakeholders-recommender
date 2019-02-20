package upc.stakeholdersrecommender.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Id;

@Entity
@Table(name = "requirementSkills")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RequirementSkills implements Serializable {
    @Id
    private Integer requirementId;

    private Integer projectIdQuery;

    // TODO set skill representation once it is defined
    //private List<Skill> skills;

    public RequirementSkills(Integer id) { requirementId=id; }

    public RequirementSkills(){};

    public RequirementSkills(Integer requirementId, Integer projectQuery) {
        this.requirementId=requirementId;
        this.projectIdQuery=projectQuery;
    }

    public Integer getRequirementId() { return requirementId; }

    public void setRequirementId(Integer requirementId) { this.requirementId = requirementId; }

   // public List<Skill> getSkills() { return skills; }

    //public void setSkills(List<Skill> skills) { this.skills = skills; }

    public Integer getProjectIdQuery() {
        return projectIdQuery;
    }

    public void setProjectIdQuery(Integer projectIdQuery) {
        this.projectIdQuery = projectIdQuery;
    }
}
