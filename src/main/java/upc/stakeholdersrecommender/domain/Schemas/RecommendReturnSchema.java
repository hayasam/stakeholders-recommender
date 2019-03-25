package upc.stakeholdersrecommender.domain.Schemas;

import java.io.Serializable;

public class RecommendReturnSchema implements Serializable, Comparable<RecommendReturnSchema> {
    private String requirement;
    private String person;
    private Double availabilityScore;
    private Double apropiatenessScore;

    public RecommendReturnSchema(String requirement, String person, Double apropiatenessScore, Double availabilityScore) {
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


    @Override
    public int compareTo(RecommendReturnSchema a) {
        if (this.apropiatenessScore > a.getApropiatenessScore())
            return 1;
        else if (this.apropiatenessScore < a.getApropiatenessScore())
            return -1;
        else {
            if (this.apropiatenessScore > a.getApropiatenessScore())
                return 1;
            else
                return -1;
        }
    }
}
