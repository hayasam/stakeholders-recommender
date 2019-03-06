package upc.stakeholdersrecommender.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RequirementId implements Serializable {

    @Column(name = "projectId")
    private Integer projectId;

    @Column(name = "requirementId")
    private Integer requirementId;

    public RequirementId() {
    }

    public RequirementId(Integer projectId, Integer personId) {
        this.projectId = projectId;
        this.requirementId = personId;
    }

    public Integer getprojectId() {
        return projectId;
    }

    public Integer getRequirementId() {
        return requirementId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequirementId)) return false;
        RequirementId that = (RequirementId) o;
        return Objects.equals(getprojectId(), that.getprojectId()) &&
                Objects.equals(getRequirementId(), that.getRequirementId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getprojectId(), getRequirementId());
    }
}

