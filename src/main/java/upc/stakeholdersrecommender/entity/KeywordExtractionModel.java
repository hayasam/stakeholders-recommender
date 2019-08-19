package upc.stakeholdersrecommender.entity;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name = "keywordModel")
public class KeywordExtractionModel {
    @Id
    @Column(length = 300)
    String id;

    @ElementCollection
    private Map<String, Integer> model;

    public Map<String, Integer> getModel() {
        return model;
    }

    public void setModel(Map<String, Integer> model) {
        this.model = model;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
