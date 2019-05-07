package upc.stakeholdersrecommender.entity;

import org.springframework.data.annotation.Id;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "person_to_replan")
public class Effort implements Serializable {
    @Id
    String id;
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
