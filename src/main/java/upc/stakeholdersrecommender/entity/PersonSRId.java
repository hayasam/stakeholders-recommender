package upc.stakeholdersrecommender.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PersonSRId implements Serializable {

    @Column(name = "projectId")
    private String projectId;

    @Column(name = "personId")
    private String personId;

    public PersonSRId() {
    }

    public PersonSRId(String projectId, String personId) {
        this.projectId = projectId;
        this.personId = personId;
    }

    public String getprojectId() {
        return projectId;
    }

    public String getPersonId() {
        return personId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonSRId)) return false;
        PersonSRId that = (PersonSRId) o;
        return Objects.equals(getprojectId(), that.getprojectId()) &&
                Objects.equals(getPersonId(), that.getPersonId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getprojectId(), getPersonId());
    }
}

