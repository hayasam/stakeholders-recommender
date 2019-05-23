package upc.stakeholdersrecommender.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SkillSRId implements Serializable {

    @Column(name = "projectId")
    private String projectId;

    @Column(name = "requirementId")
    private String skillId;

    public SkillSRId() {
    }

    public SkillSRId(String projectId, String skillId) {
        this.projectId = projectId;
        this.skillId = skillId;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getSkillId() {
        return skillId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkillSRId)) return false;
        SkillSRId that = (SkillSRId) o;
        return Objects.equals(getProjectId(), that.getProjectId()) &&
                Objects.equals(getSkillId(), that.getSkillId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProjectId(), getSkillId());
    }
}