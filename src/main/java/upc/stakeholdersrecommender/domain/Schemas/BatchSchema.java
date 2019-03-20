package upc.stakeholdersrecommender.domain.Schemas;

import upc.stakeholdersrecommender.domain.Person;
import upc.stakeholdersrecommender.domain.Project;
import upc.stakeholdersrecommender.domain.Requirement;
import upc.stakeholdersrecommender.domain.Responsible;

import java.io.Serializable;
import java.util.List;

public class BatchSchema implements Serializable {

    List<Project> projects;
    List<Person> persons;
    List<Responsible> responsibles;
    List<Requirement> requirements;
    // En return items procesats

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
}
