package upc.stakeholdersrecommender.entity;


import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "effort_project")
public class Effort implements Serializable {
    @EmbeddedId
    private ProjectSRId id;

    private HashMap<Double,Double> effortMap;

    public ProjectSRId getId() {
        return id;
    }

    public void setId(ProjectSRId id) {
        this.id = id;
    }

    public HashMap<Double, Double> getEffortMap() {
        return effortMap;
    }

    public void setEffortMap(HashMap<Double, Double> effortMap) {
        this.effortMap = effortMap;
    }
}
