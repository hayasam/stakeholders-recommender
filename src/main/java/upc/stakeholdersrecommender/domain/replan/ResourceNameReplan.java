package upc.stakeholdersrecommender.domain.replan;

import java.io.Serializable;

public class ResourceNameReplan implements Serializable {

    private String name;
    private Double availability;


    public Double getAvailability() {
        return availability;
    }

    public void setAvailability(Double availability) {
        this.availability = availability;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
