package upc.stakeholdersrecommender.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "project_to_replan")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProjectToPReplan implements Serializable {

    @Id
    private String id;

    private Integer idReplan;

    private List<String> participants;

    public ProjectToPReplan() {

    }

    public ProjectToPReplan(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Integer getIdReplan() {
        return idReplan;
    }

    public void setIdReplan(Integer idReplan) {
        this.idReplan = idReplan;
    }

    public void setID(String id) {
        this.id = id;
    }

    public void setId(String id) { this.id = id; }

    public List<String> getParticipants() { return participants; }

    public void setParticipants(List<String> participants) { this.participants = participants; }
}
