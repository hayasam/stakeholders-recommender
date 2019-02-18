package upc.stakeholdersrecommender.domain;

import java.io.Serializable;
import java.util.List;

public class PersonList implements Serializable {

    private List<Person> persons;

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
}
