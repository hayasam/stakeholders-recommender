package upc.stakeholdersrecommender.domain.Schemas;

import upc.stakeholdersrecommender.domain.Person;
import upc.stakeholdersrecommender.domain.Project;
import upc.stakeholdersrecommender.domain.Requirement;

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
