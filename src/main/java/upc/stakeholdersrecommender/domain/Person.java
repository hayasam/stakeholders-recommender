package upc.stakeholdersrecommender.domain;

import java.io.Serializable;

public class Person implements Serializable {

    private String username;

    private Double availability;


    public Person() {

    }

    public Person(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getAvailability() {
        return availability;
    }

    public void setAvailability(Double availability) {
        this.availability = availability;
    }
}
