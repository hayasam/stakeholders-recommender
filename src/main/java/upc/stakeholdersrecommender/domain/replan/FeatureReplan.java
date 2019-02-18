package upc.stakeholdersrecommender.domain.replan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import upc.stakeholdersrecommender.domain.Requirement;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FeatureReplan implements Serializable {

    private Integer id;
    private Integer code;
    private Integer effort;
    private Integer priority;

    public FeatureReplan() {

    }

    public FeatureReplan(Requirement requirement) {
        this.code = requirement.getId().hashCode();
        this.effort = 10;
        this.priority = 1;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getEffort() {
        return effort;
    }

    public void setEffort(Integer effort) {
        this.effort = effort;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
