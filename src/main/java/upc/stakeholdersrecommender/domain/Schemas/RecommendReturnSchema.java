package upc.stakeholdersrecommender.domain.Schemas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "Class representing the recommendation of a stakeholder.")
public class RecommendReturnSchema implements Serializable, Comparable<RecommendReturnSchema> {
    @ApiModelProperty(notes = "Requirement that was requested.", required = true)
    private RequirementMinimal requirement;
    @ApiModelProperty(notes = "Person that is recommended.", required = true)
    private PersonMinimal person;
    @ApiModelProperty(notes = "Availability score of the person.", example = "0.5", required = true)
    private Double availabilityScore;
    @ApiModelProperty(notes = "Appropiateness of the recommendation.", example = "0.223", required = true)
    private Double apropiatenessScore;

    public RecommendReturnSchema() {
    }

    public RecommendReturnSchema(RequirementMinimal requirement, PersonMinimal person, Double apropiatenessScore, Double availabilityScore) {
        this.requirement = requirement;
        this.person = person;
        this.availabilityScore = availabilityScore;
        this.apropiatenessScore = apropiatenessScore;

    }

    public RequirementMinimal getRequirement() {
        return requirement;
    }

    public void setRequirement(RequirementMinimal requirement) {
        this.requirement = requirement;
    }

    public PersonMinimal getPerson() {
        return person;
    }

    public void setPerson(PersonMinimal person) {
        this.person = person;
    }

    public Double getAvailabilityScore() {
        return availabilityScore;
    }

    public void setAvailabilityScore(Double availabilityScore) {
        this.availabilityScore = availabilityScore;
    }

    public Double getApropiatenessScore() {
        return apropiatenessScore;
    }

    public void setApropiatenessScore(Double apropiatenessScore) {
        this.apropiatenessScore = apropiatenessScore;
    }


    @Override
    public int compareTo(RecommendReturnSchema a) {
        if (this.apropiatenessScore < a.getApropiatenessScore())
            return 1;
        else if (this.apropiatenessScore > a.getApropiatenessScore())
            return -1;
        else {
            if (this.apropiatenessScore < a.getApropiatenessScore())
                return 1;
            else
                return -1;
        }
    }
}
