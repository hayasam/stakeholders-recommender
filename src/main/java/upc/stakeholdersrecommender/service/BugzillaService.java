package upc.stakeholdersrecommender.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import upc.stakeholdersrecommender.domain.*;
import upc.stakeholdersrecommender.domain.Schemas.BugzillaBugsSchema;
import upc.stakeholdersrecommender.domain.bugzilla.BugzillaBug;

import java.util.*;

@Service
public class BugzillaService {

    @Value("${client.bugzillaServiceUrl}")
    private String bugzillaUrl;

    private RestTemplate restTemplate = new RestTemplate();
    private List<Responsible> responsibles;
    private List<Requirement> requirements;
    private List<Person> persons;
    private List<Participant> participants;
    private List<Project> project;

    private Map<String, List<BugzillaBug>> bugs = new HashMap<String, List<BugzillaBug>>();

    public String getBugzillaUrl() {
        return bugzillaUrl;
    }

    public void setBugzillaUrl(String bugzillaUrl) {
        this.bugzillaUrl = bugzillaUrl;
    }

    public void extractInfo() {
        setBugs();
        extractPersons();
        extractResponsibles();
        extractParticipants();
        extractProject();
    }

    private void extractProject() {
        Project p = new Project();
        p.setId("Set your team's name here");
        List<String> specifiedRequirements = new ArrayList<String>();
        List<Project> projList = new ArrayList<Project>();
        projList.add(p);
        project = projList;
    }

    private void extractParticipants() {
        List<Participant> part = new ArrayList<Participant>();
        for (Person p : persons) {
            Participant participant = new Participant();
            participant.setPerson(p.getUsername());
            participant.setAvailability(100);
            participant.setProject("1");
            part.add(participant);
        }
        participants = part;
    }

    private void setBugs() {
        Integer offset = 0;
        BugzillaBugsSchema response = calltoServiceBugs("?include_fields=id,last_change_time,assigned_to,summary&status=closed&product=Platform&component=UI&creation_time=2013-01-01&limit=10000&offset=" + offset);
        List<Requirement> reqs = new ArrayList<Requirement>();
        Map<String, Integer> emailToNumber = new HashMap<String, Integer>();
        Integer counter = 0;
        // while (response.getBugs()!=null && response.getBugs().size()>0) {
        for (BugzillaBug bu : response.getBugs()) {
            if (bu.getAssigned_to() != "nobody@mozilla.org") {
                String[] test = bu.getAssigned_to().split("@");
                if (!test[1].equals("bugzilla.bugs")) {
                    List<BugzillaBug> list = new ArrayList<BugzillaBug>();
                    String assign;
                    if (emailToNumber.containsKey(bu.getAssigned_to()))
                        assign = emailToNumber.get(bu.getAssigned_to()).toString();
                    else {
                        emailToNumber.put(bu.getAssigned_to(), counter);
                        assign = counter.toString();
                        counter++;
                    }
                    if (bugs.containsKey(assign)) {
                        List<BugzillaBug> aux = bugs.get(assign);
                        aux.add(bu);
                        bugs.put(assign, aux);
                    } else {
                        list.add(bu);
                        bugs.put(assign, list);
                    }
                    Requirement requirement = new Requirement();
                    requirement.setId(bu.getId());
                    requirement.setDescription(bu.getSummary());
                    requirement.setEffort(1);
                    requirement.setModified_at(bu.getLast_change_time());
                    reqs.add(requirement);
                }
            }
        }


        response = calltoServiceBugs("?include_fields=id,last_change_time,assigned_to,summary&status=closed&product=Platform&component=SWT&creation_time=2013-01-01&limit=10000&offset=" + offset);
        for (BugzillaBug bu : response.getBugs()) {
            if (bu.getAssigned_to() != "nobody@mozilla.org") {
                String[] test = bu.getAssigned_to().split("@");
                if (!test[1].equals("bugzilla.bugs")) {
                    List<BugzillaBug> list = new ArrayList<BugzillaBug>();
                    String assign;
                    if (emailToNumber.containsKey(bu.getAssigned_to()))
                        assign = emailToNumber.get(bu.getAssigned_to()).toString();
                    else {
                        emailToNumber.put(bu.getAssigned_to(), counter);
                        assign = counter.toString();
                        counter++;
                    }
                    if (bugs.containsKey(assign)) {
                        List<BugzillaBug> aux = bugs.get(assign);
                        aux.add(bu);
                        bugs.put(assign, aux);
                    } else {
                        list.add(bu);
                        bugs.put(assign, list);
                    }
                    Requirement requirement = new Requirement();
                    requirement.setId(bu.getId());
                    requirement.setDescription(bu.getSummary());
                    requirement.setEffort(1);
                    requirement.setModified_at(bu.getLast_change_time());
                    reqs.add(requirement);
                }
            }
        }
        //}
        System.out.println(reqs.size());
        requirements = reqs;

    }
    /*

    public void testTime() {
        DateFormat helper= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        for (int i=0;i<20;++i) {
            Date dat=new Date(System.currentTimeMillis() -(3600 * 1000)*i);
            Long timeto=System.currentTimeMillis();
            BugzillaBugsSchema bugs = calltoServiceBugs("?creation_time="+dateFormat.format(dat)+"&include_fields=assigned_to,id,summary,status");
            Long finished=System.currentTimeMillis();
            Long executionTime=finished-timeto;
            System.out.println(executionTime);
        }
    }
    */


    public void extractPersons() {
        Set<String> stakeholders = new HashSet<String>();
        stakeholders.addAll(bugs.keySet());
        List<Person> pers = new ArrayList<Person>();
        for (String s : stakeholders) {
            pers.add(new Person(s));
        }
        persons = pers;
    }

    public void extractResponsibles() {
        List<Responsible> resp = new ArrayList<Responsible>();
        int i = 0;
        for (Person person : persons) {
            for (BugzillaBug b : bugs.get(person.getUsername())) {
                Responsible re = new Responsible();
                re.setPerson(person.getUsername());
                re.setRequirement(b.getId());
                resp.add(re);
            }
        }
        responsibles = resp;
    }

    /*
        public void extractRequirements() {
            List<Requirement> req = new ArrayList<Requirement>();
            Set<String> reqId = new HashSet<String>();
            int i =0;
            for (Responsible resp : responsibles) {
                reqId.add(resp.getRequirement());
            }
            for (String s : reqId) {
                List<BugzillaBug> bug = bugs.getBugs();
                Requirement requirement = new Requirement();
                requirement.setId(s);
                requirement.setDescription(bug.getSummary());
                req.add(requirement);
            }
            requirements = req;
        }
    */
    public List<Responsible> getResponsibles() {
        return responsibles;
    }

    public void setResponsibles(List<Responsible> responsibles) {
        this.responsibles = responsibles;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }


    private BugzillaBugsSchema calltoServiceBugs(String param) {
        String callUrl = bugzillaUrl + "/rest/bug" + param;
        System.out.println(callUrl);
        ResponseEntity<BugzillaBugsSchema> response = restTemplate.exchange(
                callUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<BugzillaBugsSchema>() {
                });
        return response.getBody();
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public List<Project> getProject() {
        return project;
    }

    public void setProject(List<Project> project) {
        this.project = project;
    }
}
