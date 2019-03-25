package upc.stakeholdersrecommender.domain;

import java.io.Serializable;

public class Skill implements Serializable {

    private String name;

    private Integer idReplan;

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
        this.idReplan = idReplan;
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

    public Integer getIdReplan() {
        return idReplan;
    }

    public void setIdReplan(Integer idReplan) {
        this.idReplan = idReplan;
    }
}
