package upc.stakeholdersrecommender.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;


@ApiModel(description = "Class representing a stakeholder.")
public class Person implements Serializable {
    @ApiModelProperty(notes = "Identifier of the stakeholder.", example = "John Doe", required = true)
    private String username;
    @JsonIgnore
    private Double availability;


    public Person() {

    }

    public Person(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getAvailability() {
        return availability;
    }

    public void setAvailability(Double availability) {
        this.availability = availability;
    }
}
