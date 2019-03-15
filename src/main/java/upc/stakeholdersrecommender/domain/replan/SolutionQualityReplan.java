package upc.stakeholdersrecommender.domain.replan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SolutionQualityReplan implements Serializable {

    public Double priorityQuality;
    public Double performanceQuality;
    public Double similarityQuality;
    public Double globalQuality;

}
