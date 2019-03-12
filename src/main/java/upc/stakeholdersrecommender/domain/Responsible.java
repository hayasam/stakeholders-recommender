package upc.stakeholdersrecommender.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Responsible {

    private String requirement;
    private String person;
        // En un nou return object
        //availabilityScore
        //apropiatenessScore

    public Responsible(String person, String requirement) {
        this.person=person;
        this.requirement=requirement;
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
