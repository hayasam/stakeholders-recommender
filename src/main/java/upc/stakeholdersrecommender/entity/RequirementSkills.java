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
    private String requirementId;

    // TODO set skill representation once it is defined
    //private List<Skill> skills;

    public RequirementSkills(String id) { requirementId=id; }

    public RequirementSkills(){};

    public String getRequirementId() { return requirementId; }

    public void setRequirementId(String requirementId) { this.requirementId = requirementId; }

   // public List<Skill> getSkills() { return skills; }

    //public void setSkills(List<Skill> skills) { this.skills = skills; }
}
