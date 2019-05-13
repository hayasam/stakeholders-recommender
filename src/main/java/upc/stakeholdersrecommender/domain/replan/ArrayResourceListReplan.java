package upc.stakeholdersrecommender.domain.replan;

import java.io.Serializable;
import java.util.List;

public class ArrayResourceListReplan implements Serializable {
    List<ResourceReplan> resources;

    public List<ResourceReplan> getResourceReplan() {
        return resources;
    }

    public void setResourceReplan(List<ResourceReplan> resourceListReplan) {
        this.resources = resourceListReplan;
    }
}
