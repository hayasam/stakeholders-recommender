package upc.stakeholdersrecommender.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RejectedPersonId implements Serializable {

    @Column(name = "personId", length = 300)
    private String personId;

    @Column(name = "organizationId", length = 300)
    private String organizationId;


    public RejectedPersonId() {
    }

    public RejectedPersonId(String personId, String organizationId) {
        this.personId = personId;
        this.organizationId = organizationId;
    }


    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonSRId)) return false;
        PersonSRId that = (PersonSRId) o;
        return Objects.equals(getPersonId(), that.getPersonId())
                &&
                Objects.equals(getOrganizationId(), that.getOrganizationId());
    }


    @Override
    public int hashCode() {
        return Objects.hash(getPersonId(), getOrganizationId());
    }


}