package upc.stakeholdersrecommender.domain.Schemas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import upc.stakeholdersrecommender.domain.Requirement;

import java.io.Serializable;

@ApiModel(description = "Class representing the information needed to generate a recommendation for a requirement.")
public class RecommendSchema implements Serializable {
    @ApiModelProperty(notes = "The identifier of the requirement to recommend.", required = true)
    private Requirement requirement;
    @ApiModelProperty(notes = "The identifier of the project the requirement belongs to.", required = true)
    private ProjectMinimal project;
    @ApiModelProperty(notes = "The identifier of the person who asks for this recommendation.", required = true)
    private PersonMinimal user;

    public Requirement getRequirement() {
        return requirement;
    }

    public void setRequirement(Requirement requirement) {
        this.requirement = requirement;
    }

    public ProjectMinimal getProject() {
        return project;
    }

    public void setProject(ProjectMinimal project) {
        this.project = project;
    }

    public PersonMinimal getUser() {
        return user;
    }

    public void setUser(PersonMinimal user) {
        this.user = user;
    }
}
