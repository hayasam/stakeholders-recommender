package upc.stakeholdersrecommender.domain.Schemas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing the mapping between effort points and hours.")
public class EffortHour {
    @ApiModelProperty(notes = "The effort.", example="\"1.0\"",required = true)
    private Double effort;
    @ApiModelProperty(notes = "The hours.", example="\"1.0\"",required = true)
    private Double hours;

    public Double getEffort() {
        return effort;
    }

    public void setEffort(Double effort) {
        this.effort = effort;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }
}
