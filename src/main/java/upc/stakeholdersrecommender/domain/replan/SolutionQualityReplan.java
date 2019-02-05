package upc.stakeholdersrecommender.domain.replan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SolutionQualityReplan implements Serializable {

    private Double priorityQuality;
    private Double performanceQuality;
    private Double similarityQuality;
    private Double globalQuality;

}
