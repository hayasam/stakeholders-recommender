package upc.stakeholdersrecommender.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import upc.stakeholdersrecommender.domain.RequirementList;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class ReturnObject implements Serializable {

    private String id;
    private Set<String> requirements;

    public ReturnObject() {

    }

    public ReturnObject(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<String> getRequirement() {
        return requirements;
    }

    public void setRequirement(Set<String> requirements) {
        this.requirements = requirements;
    }
}
