package upc.stakeholdersrecommender.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "Class representing the relation of a person working for a project, and the time this person has with the project.")
public class Participant implements Serializable {

    @ApiModelProperty(notes = "Identifier of the project.", example = "\"1\"", required = true)
    private String project;
    @ApiModelProperty(notes = "Identifier of the person.", example = "John Doe", required = true)
    private String person;
    @ApiModelProperty(notes = "Hours the person has for this project, necessary if parameter withAvailability is true.", example = "40", required = true)
    private Integer availability;

    public Participant() {
    }

    public Participant(String person, String project) {
        this.person = person;
        this.project = project;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public Integer getAvailability() {
        return availability;
    }

    public void setAvailability(Integer availability) {
        this.availability = availability;
    }
}
