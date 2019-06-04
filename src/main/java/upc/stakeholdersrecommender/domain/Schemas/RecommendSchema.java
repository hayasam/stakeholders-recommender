package upc.stakeholdersrecommender.domain.Schemas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "Class representing the information needed to generate a recommendation for a requirement.")
public class RecommendSchema implements Serializable {
    @ApiModelProperty(notes = "The identifier of the requirement to recommend.", example="1",required = true)
    private String requirement;
    @ApiModelProperty(notes = "The identifier of the project the requirement belongs to.", example="1",required = true)
    private String project;
    @ApiModelProperty(notes = "The identifier of the person who asks for this recommendation.", example="John Doe",required = true)
    private String user;

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
