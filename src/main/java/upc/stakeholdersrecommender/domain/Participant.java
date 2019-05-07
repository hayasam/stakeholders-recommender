package upc.stakeholdersrecommender.domain;

import java.io.Serializable;

public class Participant implements Serializable {

    private String project;
    private String person;
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

    public void setAvaiability(Integer availability) {
        this.availability = availability;
    }
}
