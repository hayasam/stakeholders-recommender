package upc.stakeholdersrecommender.domain.bugzilla;

import java.io.Serializable;

public class BugzillaIdentifier implements Serializable {

    String id;
    String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
