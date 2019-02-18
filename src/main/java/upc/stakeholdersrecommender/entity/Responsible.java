package upc.stakeholdersrecommender.entity;

public class Responsible {

    private Requirement requirement;
    private Person person;


    public Requirement getRequirement() {
        return requirement;
    }

    public void setRequirement(Requirement requirement) {
        this.requirement = requirement;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }


}
