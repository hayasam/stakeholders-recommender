package upc.stakeholdersrecommender.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Skill implements Serializable {
    @Column(length = 300)
    private String name;

    private Double weight;

    public Skill() {

    }

    public Skill(String name) {
        this.name = name;
    }

    public Skill(String name, Double weight) {
        this.name = name;
        this.weight = weight;
    }

    public Skill(String name, Double weight, Integer idReplan) {
        this.name = name;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

}
