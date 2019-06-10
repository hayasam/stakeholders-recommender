package upc.stakeholdersrecommender.domain.Schemas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing a stakeholder, only with ID.")
public class PersonMinimal {
    @ApiModelProperty(notes = "Username of stakeholder.", example = "John Doe", required = true)
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
