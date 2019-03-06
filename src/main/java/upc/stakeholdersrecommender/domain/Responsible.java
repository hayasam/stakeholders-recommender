package upc.stakeholdersrecommender.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Responsible {

    private Integer requirement;
    private Person person;


    public Responsible(String person, Integer requirement) {
        this.person = new Person();
        this.person.setUsername(person);
        this.requirement=requirement;
    }

    public Integer getRequirement() {
        return requirement;
    }

    public void setRequirement(Integer requirements) {
        this.requirement = requirements;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }


}
