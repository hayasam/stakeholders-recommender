package upc.stakeholdersrecommender.domain.Schemas;

import upc.stakeholdersrecommender.domain.Person;
import upc.stakeholdersrecommender.domain.Requirement;
import upc.stakeholdersrecommender.domain.Responsible;
import upc.stakeholdersrecommender.domain.bugzilla.BugzillaBug;
import upc.stakeholdersrecommender.domain.bugzilla.BugzillaIdentifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BugzillaBugsSchema implements Serializable {

    List<BugzillaBug> bugs;

    public Set<String> getStakeholders() {
        Set<String> name= new HashSet<String>();
        Set<String> id= new HashSet<String>();
        for (BugzillaBug bug: bugs) {
            name.add(bug.getAssigned_to_detail().getName());
            id.add(bug.getAssigned_to_detail().getId());
            name.add(bug.getCreator_detail().getName());
            id.add(bug.getCreator_detail().getId());
            for (BugzillaIdentifier identifier: bug.getCc_detail()) {
                id.add(identifier.getId());
                name.add(identifier.getId());
            }
        }
        return name;
    }

    public List<Responsible> getResponsibles(String user) {
        List<Responsible> resp= new ArrayList<Responsible>();
        for (BugzillaBug bug: bugs) {
            Responsible re= new Responsible();
            re.setPerson(user);
            re.setRequirement(bug.getId());
            resp.add(new Responsible());
        }
        return resp;
    }

    public List<BugzillaBug> getBugs() {
        return bugs;
    }

    public void setBugs(List<BugzillaBug> bugs) {
        this.bugs = bugs;
    }

}
