package upc.stakeholdersrecommender.domain.replan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Plan implements Serializable {

    private Integer id;
    private String created_at;
    private Integer release_id;
    private Boolean isCurrent;
    private SolutionQualityReplan solutionQuality;
    private List<ResourceReplan> resources;

    public Plan() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Integer getRelease_id() {
        return release_id;
    }

    public void setRelease_id(Integer release_id) {
        this.release_id = release_id;
    }

    public Boolean getCurrent() {
        return isCurrent;
    }

    public void setCurrent(Boolean current) {
        isCurrent = current;
    }

    public SolutionQualityReplan getSolutionQuality() {
        return solutionQuality;
    }

    public void setSolutionQuality(SolutionQualityReplan solutionQuality) {
        this.solutionQuality = solutionQuality;
    }

    public List<ResourceReplan> getResources() {
        return resources;
    }

    public void setResources(List<ResourceReplan> resources) {
        this.resources = resources;
    }

    public Map<String, Set<String>> getRequirementStakeholder() {
        TreeMap<String, Set<String>> result=new TreeMap<String,Set<String>>();
        for (ResourceReplan res: resources) {
            result.put(res.getName(),res.getFeaturesWorkedOn());
        }
        return result;
    }
}
