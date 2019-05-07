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
    private Integer[] effort=new Integer[5];

    public Integer[] getEffort() {
        return effort;
    }

    public void setEffort(Integer[] effort) {
        this.effort = effort;
    }
}
