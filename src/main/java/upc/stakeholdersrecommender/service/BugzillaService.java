package upc.stakeholdersrecommender.service;


import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import upc.stakeholdersrecommender.domain.Person;
import upc.stakeholdersrecommender.domain.Requirement;
import upc.stakeholdersrecommender.domain.Responsible;
import upc.stakeholdersrecommender.domain.Schemas.BugzillaBugsSchema;
import upc.stakeholdersrecommender.domain.bugzilla.BugzillaBug;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BugzillaService {

    @Value("${client.bugzillaServiceUrl}")
    private String bugzillaUrl;

    private RestTemplate restTemplate = new RestTemplate();
    private List<Responsible> responsibles;
    private List<Requirement> requirements;
    private List<Person> persons;

    private Map<String,List<BugzillaBug>> bugs=new HashMap<String,List<BugzillaBug>>();

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
    }

    private void setBugs() {
        Integer offset=0;
        BugzillaBugsSchema response=calltoServiceBugs("?include_fields=id,assigned_to,summary&status=CLOSED&creation_time=1990-01-01&limit=10000&offset="+offset);
        List<Requirement> reqs= new ArrayList<Requirement>();
        while (response.getBugs()!=null && response.getBugs().size()>0) {
            for (BugzillaBug bu:response.getBugs()) {
                List<BugzillaBug> list= new ArrayList<BugzillaBug>();
                if (bugs.containsKey(bu.getAssigned_to())) {
                    List<BugzillaBug> aux=bugs.get(bu.getAssigned_to());
                    aux.add(bu);
                    bugs.put(bu.getAssigned_to(),aux);
                }
                else {
                    list.add(bu);
                    bugs.put(bu.getAssigned_to(),list);
                }
                Requirement requirement = new Requirement();
                requirement.setId(bu.getId());
                requirement.setDescription(bu.getSummary());
                reqs.add(requirement);
            }
            offset=offset+10000;
            response=calltoServiceBugs("?include_fields=id,assigned_to,summary&status=CLOSED&creation_time=1990-01-01&limit=10000&offset="+offset);
        }
        requirements=reqs;

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
        Set<String> stakeholders= new HashSet<String>();
        stakeholders.addAll(bugs.keySet());
        List<Person> pers = new ArrayList<Person>();
        for (String s : stakeholders) {
            pers.add(new Person(s));
        }
        persons = pers;
    }

    public void extractResponsibles() {
        List<Responsible> resp = new ArrayList<Responsible>();
        int i =0;
        for (Person person : persons) {
            for (BugzillaBug b: bugs.get(person.getUsername())) {
                Responsible re= new Responsible();
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
    private BugzillaBug calltoService(String param) {
        String callUrl = bugzillaUrl + "/rest/bug" + param;
        System.out.println(callUrl);
        ResponseEntity<BugzillaBug> response = restTemplate.exchange(
                callUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<BugzillaBug>() {
                });
        return response.getBody();
    }
}
