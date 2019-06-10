package upc.stakeholdersrecommender.entity;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "effort_project")
public class Effort implements Serializable {
    @Id
    private String id;

    private HashMap<String,Double> effortMap;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Double> getEffortMap() {
        return effortMap;
    }

    public void setEffortMap(HashMap<String, Double> effortMap) {
        this.effortMap = effortMap;
    }
}
