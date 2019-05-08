package upc.stakeholdersrecommender.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "effort_project")
public class Effort implements Serializable {
    @Id
    private String id;

    @OrderColumn
    private Double[] effort=new Double[5];

    public Double[] getEffort() {
        return effort;
    }

    public void setEffort(Double[] effort) {
        this.effort = effort;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
