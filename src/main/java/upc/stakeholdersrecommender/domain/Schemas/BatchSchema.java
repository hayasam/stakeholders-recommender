package upc.stakeholdersrecommender.domain.Schemas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import upc.stakeholdersrecommender.domain.*;

import java.io.Serializable;
import java.util.List;

@ApiModel(description = "Class representing the information needed for the recommendation of stakeholders.")
public class BatchSchema implements Serializable {
    @ApiModelProperty(notes = "List of projects.", required = true)
    List<Project> projects;
    @ApiModelProperty(notes = "List of stakeholders.", required = true)
    List<Person> persons;
    @ApiModelProperty(notes = "List of responsibles.", required = true)
    List<Responsible> responsibles;
    @ApiModelProperty(notes = "List of requirements.", required = true)
    List<Requirement> requirements;
    @ApiModelProperty(notes = "List of participants.", required = true)
    List<Participant> participants;

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public List<Responsible> getResponsibles() {
        return responsibles;
    }

    public void setResponsibles(List<Responsible> responsibles) {
        this.responsibles = responsibles;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }
}
