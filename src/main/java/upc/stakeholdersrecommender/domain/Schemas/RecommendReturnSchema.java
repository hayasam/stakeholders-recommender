package upc.stakeholdersrecommender.domain.Schemas;

import java.io.Serializable;

public class RecommendReturnSchema implements Serializable {
    private String requirement;
    private String person;
    private Double availabilityScore;
    private Double apropiatenessScore;

    public RecommendReturnSchema (String requirement,String person,Double availabilityScore,Double apropiatenessScore) {
        this.requirement = requirement;
        this.person = person;
        this.availabilityScore = availabilityScore;
        this.apropiatenessScore = apropiatenessScore;

    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
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
}
