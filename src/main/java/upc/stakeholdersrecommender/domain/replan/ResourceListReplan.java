package upc.stakeholdersrecommender.domain.replan;

import java.io.Serializable;

public class ResourceListReplan implements Serializable {

    private Integer resource_id;

    public ResourceListReplan() {

    }

    public ResourceListReplan(Integer resource_id) {
        this.resource_id = resource_id;
    }

    public Integer getResource_id() {
        return resource_id;
    }

    public void setResource_id(Integer resource_id) {
        this.resource_id = resource_id;
    }
}
