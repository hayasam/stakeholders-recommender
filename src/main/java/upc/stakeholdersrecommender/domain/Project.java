package upc.stakeholdersrecommender.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Project implements Serializable {

    private String id;
    private Integer created_at;
    private List<String> specifiedRequirements;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Integer created_at) {
        this.created_at = created_at;
    }

    public List<String> getSpecifiedRequirements() {
        return specifiedRequirements;
    }

    public void setSpecifiedRequirements(List<String> specifiedRequirements) {
        this.specifiedRequirements = specifiedRequirements;
    }

}
