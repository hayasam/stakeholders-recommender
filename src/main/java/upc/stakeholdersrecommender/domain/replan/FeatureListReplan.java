package upc.stakeholdersrecommender.domain.replan;

import java.io.Serializable;

public class FeatureListReplan implements Serializable {

    private String feature_id;

    public FeatureListReplan() {

    }

    public FeatureListReplan(String feature_id) {
        this.feature_id = feature_id;
    }

    public String getFeature_id() {
        return feature_id;
    }

    public void setFeature_id(String feature_id) {
        this.feature_id = feature_id;
    }
}
