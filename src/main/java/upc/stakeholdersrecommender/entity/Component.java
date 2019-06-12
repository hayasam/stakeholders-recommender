package upc.stakeholdersrecommender.entity;

public class Component {

    private String name;

    private Double weight;

    public Component() {

    }

    public Component(String name) {
        this.name = name;
    }

    public Component(String name, Double weight) {
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
