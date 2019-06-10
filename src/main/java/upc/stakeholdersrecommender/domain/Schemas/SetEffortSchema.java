package upc.stakeholdersrecommender.domain.Schemas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;
//Change so that it uses an array of string,float


@ApiModel(description = "Class representing the mapping between effort and hours that is to be set.")
public class SetEffortSchema implements Serializable {
    @ApiModelProperty(notes = "Array of effort with their respective hours to map.",required = true)
    private List<EffortHour> effortToHour;

    public List<EffortHour> getEffortToHour() {
        return effortToHour;
    }

    public void setEffortToHour(List<EffortHour> effortToHour) {
        this.effortToHour = effortToHour;
    }
}
