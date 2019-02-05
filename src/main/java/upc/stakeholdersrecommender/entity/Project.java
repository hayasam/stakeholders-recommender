package upc.stakeholdersrecommender.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Project implements Serializable {

    private String id;
    private String name;
    private List<String> specifiedRequirements;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSpecifiedRequirements() {
        return specifiedRequirements;
    }

    public void setSpecifiedRequirements(List<String> specifiedRequirements) {
        this.specifiedRequirements = specifiedRequirements;
    }
}
