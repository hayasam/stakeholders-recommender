package upc.stakeholdersrecommender.domain.Schemas;

import java.io.Serializable;

public class RequirementDocument implements Serializable {

    private String description;
    private String id;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
