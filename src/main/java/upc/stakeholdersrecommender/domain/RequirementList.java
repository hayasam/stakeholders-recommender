package upc.stakeholdersrecommender.domain;

import upc.stakeholdersrecommender.entity.Requirement;

import java.io.Serializable;
import java.util.List;

public class RequirementList implements Serializable {

    public List<Requirement> requirements;

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }
}
