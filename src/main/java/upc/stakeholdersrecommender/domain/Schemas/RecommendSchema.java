package upc.stakeholdersrecommender.domain.Schemas;

import java.io.Serializable;

public class RecommendSchema implements Serializable {

    private String requirement;
    private String project;
    private String user;

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
