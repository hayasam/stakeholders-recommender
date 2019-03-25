package upc.stakeholdersrecommender.domain;

import java.io.Serializable;

public class Responsible implements Serializable {

    private String requirement;
    private String person;

    public Responsible() {

    }

    public Responsible(String person, String requirement) {
        this.person = person;
        this.requirement = requirement;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirements) {
        this.requirement = requirements;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }


}
