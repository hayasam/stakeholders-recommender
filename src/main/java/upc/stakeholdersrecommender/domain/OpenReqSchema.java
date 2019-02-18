package upc.stakeholdersrecommender.domain;

import upc.stakeholdersrecommender.entity.Person;
import upc.stakeholdersrecommender.entity.Project;
import upc.stakeholdersrecommender.entity.Requirement;
import upc.stakeholdersrecommender.entity.Responsible;

import java.io.Serializable;
import java.util.List;

public class OpenReqSchema implements Serializable {

    List<Project> projects;
    List<Requirement> requirements;
    List<Person> persons;
    List<Responsible> responsibles;

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public List<Responsible> getResponsibles() { return responsibles; }

    public void setResponsibles(List<Responsible> responsibles) { this.responsibles = responsibles; }
}
