package upc.stakeholdersrecommender.domain;

import upc.stakeholdersrecommender.entity.Person;
import upc.stakeholdersrecommender.entity.Requirement;

import java.io.Serializable;

public class RecommendRejectSchema implements Serializable{

    private Person user;
    private Person rejected;
    private Requirement requirement;



    public Person getUser() { return user; }

    public void setUser(Person user) { this.user = user; }

    public Person getRejected() { return rejected; }

    public void setRejected(Person rejected) { this.rejected = rejected; }

    public Requirement getRequirement() { return requirement; }

    public void setRequirement(Requirement requirement) { this.requirement = requirement; }

}
