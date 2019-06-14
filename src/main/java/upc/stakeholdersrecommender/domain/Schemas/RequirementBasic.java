package upc.stakeholdersrecommender.domain.Schemas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing a requirement with only their id, effort and hours.")
public class RequirementBasic {
    @ApiModelProperty(notes = "The id of the requirement.", example="\"1\"",required = true)
    private String id;
    @ApiModelProperty(notes = "The effort points of the requirement.", example="\"1\"",required = true)
    private String effort;
    @ApiModelProperty(notes = "The hours it took to finish the requirement.", example="\"1.0\"",required = true)
    private Double hours;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEffort() {
        return effort;
    }

    public void setEffort(String effort) {
        this.effort = effort;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }
}
