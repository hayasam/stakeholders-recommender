package upc.stakeholdersrecommender.domain;

import upc.stakeholdersrecommender.entity.Person;
import upc.stakeholdersrecommender.entity.Project;
import upc.stakeholdersrecommender.entity.Requirement;

import java.io.Serializable;

public class RecommendSchema implements Serializable {

    private Requirement requirement;
    private Project project;
    private Person user;

    public Requirement getRequirement() { return requirement; }

    public void setRequirement(Requirement requirement) { this.requirement = requirement; }

    public Project getProject() { return project; }

    public void setProject(Project project) { this.project = project; }

    public Person getUser() { return user; }

    public void setUser(Person user) { this.user = user; }
}
