package upc.stakeholdersrecommender.domain.Schemas;

import upc.stakeholdersrecommender.domain.Person;
import upc.stakeholdersrecommender.domain.Project;
import upc.stakeholdersrecommender.domain.Requirement;

import java.io.Serializable;

public class RecommendSchema implements Serializable {

    private String requirement;
    private String project;
    private String user;

    //Retornar extra appropiateness y availability

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
