package upc.stakeholdersrecommender.domain.Schemas;


import java.io.Serializable;
import java.util.List;

public class ExtractedRequirement implements Serializable {

    private String id;
    private String description;
    private List<String> keywords;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
