package upc.stakeholdersrecommender.domain.replan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DaySlot implements Serializable {

    private String begins;
    private String ends;
    private String slotStatus;
    private String feature_id;

    public DaySlot() {

    }

    public String getBegins() {
        return begins;
    }

    public void setBegins(String begins) {
        this.begins = begins;
    }

    public String getEnds() {
        return ends;
    }

    public void setEnds(String ends) {
        this.ends = ends;
    }

    public String getSlotStatus() {
        return slotStatus;
    }

    public void setSlotstatus(String slotStatus) {
        this.slotStatus = slotStatus;
    }

    public String getFeature_id() {
        return feature_id;
    }

    public void setFeature_id(String feature_id) {
        this.feature_id = feature_id;
    }
}
