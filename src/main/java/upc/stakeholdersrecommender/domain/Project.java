package upc.stakeholdersrecommender.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

@ApiModel(description = "Class representing a project.")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project implements Serializable {
    @ApiModelProperty(notes = "Identifier of the project.", example = "1", required = true)
    private String id;
    @ApiModelProperty(notes = "List of requirement identifiers.", example = "[\"1\"]", required = true)
    private List<String> specifiedRequirements;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getSpecifiedRequirements() {
        return specifiedRequirements;
    }

    public void setSpecifiedRequirements(List<String> specifiedRequirements) {
        this.specifiedRequirements = specifiedRequirements;
    }

}
