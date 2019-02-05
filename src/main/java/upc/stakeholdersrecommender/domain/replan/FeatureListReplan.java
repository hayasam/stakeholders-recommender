package upc.stakeholdersrecommender.domain.replan;

import java.io.Serializable;

public class FeatureListReplan implements Serializable {

    private Integer feature_id;

    public FeatureListReplan() {

    }

    public FeatureListReplan(Integer feature_id) {
        this.feature_id = feature_id;
    }

    public Integer getFeature_id() {
        return feature_id;
    }

    public void setFeature_id(Integer feature_id) {
        this.feature_id = feature_id;
    }
}
