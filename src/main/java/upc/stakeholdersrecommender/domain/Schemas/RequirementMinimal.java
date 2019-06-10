package upc.stakeholdersrecommender.domain.Schemas;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing a requirement, only with ID.")
public class RequirementMinimal {
    @ApiModelProperty(notes = "Identifier of requirement.", example = "1", required = true)
    private String id;


    public RequirementMinimal(String name) {
        this.id=name;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
