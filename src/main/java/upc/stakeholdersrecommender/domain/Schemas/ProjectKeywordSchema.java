package upc.stakeholdersrecommender.domain.Schemas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(description = "Class representing a project with its list of requirements with their keywords.")
public class ProjectKeywordSchema {
    @ApiModelProperty(notes = "Requirements with their keywords.", required = true)
    List<KeywordReturnSchema> requirements;
    @ApiModelProperty(notes = "ID of the project.", example = "1", required = true)
    String projectId;

    public List<KeywordReturnSchema> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<KeywordReturnSchema> requirements) {
        this.requirements = requirements;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
