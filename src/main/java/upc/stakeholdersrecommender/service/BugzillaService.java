package upc.stakeholdersrecommender.service;


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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BugzillaService {

    @Value("${client.bugzillaServiceUrl}")
    private String bugzillaUrl;

    private RestTemplate restTemplate = new RestTemplate();
    private List<Responsible> responsibles;
    private List<Requirement> requirements;
    private List<Person> persons;

    public String getBugzillaUrl() {
        return bugzillaUrl;
    }

    public void setBugzillaUrl(String bugzillaUrl) {
        this.bugzillaUrl = bugzillaUrl;
    }

    public void extractInfo() {
        extractPersons();
        extractResponsibles();
        extractRequirements();
    }


    public void extractPersons() {
        BugzillaBugsSchema bugs = calltoServiceBugs("?creation_time=2014-01-1&include_fields=assigned_to,creator,cc");
        Set<String> stakeholders = bugs.getStakeholders();
        List<Person> pers = new ArrayList<Person>();
        for (String s : stakeholders) {
            pers.add(new Person(s));
        }
        persons = pers;
    }

    public void extractResponsibles() {
        List<Responsible> resp = new ArrayList<Responsible>();
        for (Person person : persons) {
            BugzillaBugsSchema bugs = calltoServiceBugs("?assigned_to=" + person.getUsername());
            resp.addAll(bugs.getResponsibles(person.getUsername()));
        }
        responsibles = resp;
    }

    public void extractRequirements() {
        List<Requirement> req = new ArrayList<Requirement>();
        Set<String> reqId = new HashSet<String>();
        for (Responsible resp : responsibles) {
            reqId.add(resp.getRequirement());
        }
        for (String s : reqId) {
            BugzillaBug bug = calltoService("/"+s);
            Requirement requirement = new Requirement();
            requirement.setId(s);
            requirement.setDescription(bug.getSummary());
            req.add(requirement);
        }
        requirements = req;
    }

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
