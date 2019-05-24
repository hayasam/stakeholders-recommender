package upc.stakeholdersrecommender.domain.Schemas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KeywordExtractSchema implements Serializable {
    List<RequirementDocument> requirements = new ArrayList<RequirementDocument>();

    public List<RequirementDocument> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<RequirementDocument> requirements) {
        this.requirements = requirements;
    }

    public void addRequirement(RequirementDocument doc) {
        requirements.add(doc);
    }
}
