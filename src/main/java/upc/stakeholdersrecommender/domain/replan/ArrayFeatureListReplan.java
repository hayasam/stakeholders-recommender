package upc.stakeholdersrecommender.domain.replan;

import java.io.Serializable;
import java.util.List;

public class ArrayFeatureListReplan implements Serializable {

    List<FeatureReplan> features;

    public List<FeatureReplan> getFeatureReplan() {
        return features;
    }

    public void setFeatureReplan(List<FeatureReplan> featureListReplan) {
        this.features = featureListReplan;
    }
}
