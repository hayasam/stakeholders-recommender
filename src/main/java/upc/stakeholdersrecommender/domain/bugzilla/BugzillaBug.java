package upc.stakeholdersrecommender.domain.bugzilla;

import java.io.Serializable;
import java.util.List;

public class BugzillaBug implements Serializable {

    BugzillaIdentifier assigned_to_detail;
    BugzillaIdentifier creator_detail;
    List<BugzillaIdentifier> cc_detail;
    String id;
    String summary;

    public BugzillaIdentifier getAssigned_to_detail() {
        return assigned_to_detail;
    }

    public void setAssigned_to_detail(BugzillaIdentifier assigned_to_detail) {
        this.assigned_to_detail = assigned_to_detail;
    }

    public BugzillaIdentifier getCreator_detail() {
        return creator_detail;
    }

    public void setCreator_detail(BugzillaIdentifier creator_detail) {
        this.creator_detail = creator_detail;
    }

    public List<BugzillaIdentifier> getCc_detail() {
        return cc_detail;
    }

    public void setCc_detail(List<BugzillaIdentifier> cc_detail) {
        this.cc_detail = cc_detail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
