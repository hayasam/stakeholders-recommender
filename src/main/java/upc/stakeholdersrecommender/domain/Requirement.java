package upc.stakeholdersrecommender.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import upc.stakeholdersrecommender.domain.Schemas.RequirementPart;
import upc.stakeholdersrecommender.entity.Skill;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApiModel(description = "Class representing a requirement.")
public class Requirement implements Serializable {
    @ApiModelProperty(notes = "Identifier of the requirement.", example = "\"1\"", required = true)
    private String id;
    @ApiModelProperty(notes = "How much effort the requirement will take. It is not required if using the parameter withAvailability as false", example = "\"3\"", required = false)
    private String effort;
    @ApiModelProperty(notes = "The requirement parts of the requirement", required = false)
    private List<RequirementPart> requirementParts;

    @JsonIgnore
    private List<Skill> skills = new ArrayList<Skill>();

    @JsonIgnore
    private Date modified;
    @ApiModelProperty(notes = "The requirement's description.", example = "This is not really a requirement, but an example", required = true)
    private String description;
    @ApiModelProperty(notes = "When was the requirement last modified.", example = "2014-01-13T15:14:17Z", required = false)
    private String modified_at;

    public Requirement() {

    }

    public Requirement(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addSkill(Skill auxiliar) {
        this.skills.add(auxiliar);
    }

    public String getEffort() {
        return effort;
    }

    public void setEffort(String effort) {
        this.effort = effort;
    }

    public String getModified_at() {
        return modified_at;
    }

    public void setModified_at(String modified_at) {
        this.modified_at = modified_at;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public List<RequirementPart> getRequirementParts() {
        return requirementParts;
    }

    public void setRequirementParts(List<RequirementPart> requirementParts) {
        this.requirementParts = requirementParts;
    }
}
