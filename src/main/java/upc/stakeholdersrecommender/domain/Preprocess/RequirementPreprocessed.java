package upc.stakeholdersrecommender.domain.Preprocess;

import java.util.List;

public class RequirementPreprocessed {
    private String id;
    private List<String> title_tokens;
    private List<String> description_tokens;


    public RequirementPreprocessed() {

    }

    public String getDescription() {
        String fused = "";
        for (String t : title_tokens) {
            fused = fused + " " + t;
        }
        for (String d : description_tokens) {
            fused = fused + " " + d;
        }
        return fused;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getTitle_tokens() {
        return title_tokens;
    }

    public void setTitle_tokens(List<String> title_tokens) {
        this.title_tokens = title_tokens;
    }

    public List<String> getDescription_tokens() {
        return description_tokens;
    }

    public void setDescription_tokens(List<String> description_tokens) {
        this.description_tokens = description_tokens;
    }
}
