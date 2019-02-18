package upc.stakeholdersrecommender.domain;

import java.io.Serializable;

public class Skill implements Serializable {

    private String name;

    public Skill() {

    }

    public Skill(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
