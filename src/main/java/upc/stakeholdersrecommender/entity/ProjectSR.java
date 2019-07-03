package upc.stakeholdersrecommender.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "projectSR")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProjectSR implements Serializable {

    @EmbeddedId
    private ProjectSRId id;

    @ElementCollection
    private List<String> participants;
    @Column(length = 300)
    private String organization;

    private Boolean rake;

    public ProjectSR() {

    }

    public ProjectSR(ProjectSRId newId) {
        this.id=newId;
    }

    public ProjectSRId getId() {
        return id;
    }

    public void setId(ProjectSRId id) {
        this.id = id;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public Boolean getRake() {
        return rake;
    }

    public void setRake(Boolean rake) {
        this.rake = rake;
    }
}
