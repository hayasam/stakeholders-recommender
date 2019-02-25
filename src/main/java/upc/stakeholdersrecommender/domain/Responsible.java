package upc.stakeholdersrecommender.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Responsible {

    private List<Requirement> requirements = new ArrayList<Requirement>();
    private Person person;


    public Responsible(String person, Set<String> requirement) {
        this.person = new Person();
        this.person.setUsername(person);
        for (String s : requirement) {
            this.requirements.add(new Requirement(s));
        }
    }

    public List<Requirement> getRequirement() {
        return requirements;
    }

    public void setRequirement(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }


}
