package upc.stakeholdersrecommender.domain.replan;

import java.io.Serializable;

public class ResourceListReplan implements Serializable {

    private String resource_id;

    public ResourceListReplan() {

    }

    public ResourceListReplan(String resource_id) {
        this.resource_id = resource_id;
    }

    public String getResource_id() {
        return resource_id;
    }

    public void setResource_id(String resource_id) {
        this.resource_id = resource_id;
    }
}
