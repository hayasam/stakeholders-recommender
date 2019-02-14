package upc.stakeholdersrecommender.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

public class ReturnObject implements Serializable {

    private String id;
    private List<String> featureID;

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

    public List<String> getFeatureID() {
        return featureID;
    }

    public void setFeatureID(List<String> featureID) {
        this.featureID = featureID;
    }
}
