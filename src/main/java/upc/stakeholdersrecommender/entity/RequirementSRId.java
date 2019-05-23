package upc.stakeholdersrecommender.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RequirementSRId implements Serializable {

    @Column(name = "projectId")
    private String projectId;

    @Column(name = "requirementId")
    private String requirementId;

    public RequirementSRId() {
    }

    public RequirementSRId(String projectId, String personId) {
        this.projectId = projectId;
        this.requirementId = personId;
    }

    public String getprojectId() {
        return projectId;
    }

    public String getRequirementId() {
        return requirementId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequirementSRId)) return false;
        RequirementSRId that = (RequirementSRId) o;
        return Objects.equals(getprojectId(), that.getprojectId()) &&
                Objects.equals(getRequirementId(), that.getRequirementId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getprojectId(), getRequirementId());
    }
}

