package upc.stakeholdersrecommender.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "requirement_to_replan")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RequirementToFeature {

        @Id
        private String id;

        private Integer idReplan;

        public RequirementToFeature() {

        }

        public RequirementToFeature(String id) {
            this.id = id;
        }

        public String getID() {
            return id;
        }

        public void setID_Replan(Integer idReplan) {
            this.idReplan = idReplan;
        }

        public Integer getID_Replan() { return idReplan; }

        public void setID(String id) {
            this.id = id;
        }



}
