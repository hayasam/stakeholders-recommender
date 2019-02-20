package upc.stakeholdersrecommender.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Id;

@Entity
@Table(name = "project_to_replan")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProjectToPReplan {

    @Id
    private String id;

    private Integer idReplan;

    public ProjectToPReplan() {

    }

    public ProjectToPReplan(String id) {
        this.id = id;
    }

    public String getid() {
        return id;
    }

    public void setIdReplan(Integer idReplan) {
        this.idReplan = idReplan;
    }

    public Integer getIdReplan() { return idReplan; }

    public void setID(String id) {
        this.id = id;
    }


}
