package upc.stakeholdersrecommender.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "requirement_to_replan")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SkillSR implements Serializable {

    @EmbeddedId
    private SkillSRId id;

}
