package upc.stakeholdersrecommender.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import upc.stakeholdersrecommender.domain.Person;
import upc.stakeholdersrecommender.domain.Project;
import upc.stakeholdersrecommender.domain.Schemas.FeatureSkill;
import upc.stakeholdersrecommender.domain.Schemas.ResourceSkill;
import upc.stakeholdersrecommender.domain.Skill;
import upc.stakeholdersrecommender.domain.replan.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ReplanService {

    @Value("${replan.client.serviceUrl}")
    private String replanUrl;

    private RestTemplate restTemplate = new RestTemplate();

    public Project getProject(String id) {
        ResponseEntity<Project> response = restTemplate.exchange(
                replanUrl + "/projects/" + id,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Project>() {
                });
        Project project = response.getBody();
        return project;
    }

    public ProjectReplan createProject(Project p) {
        ProjectReplan projectReplan = new ProjectReplan(p);

        ProjectReplan createdPlan = restTemplate.postForObject(
                replanUrl + "/projects",
                projectReplan,
                ProjectReplan.class);

        return createdPlan;
    }

    public ResourceReplan createResource(Person p, String projectId, Double availability) {
        ResourceReplan resourceReplan = new ResourceReplan(p, availability);

        ResourceReplan createdResource = restTemplate.postForObject(
                replanUrl + "/projects/" + projectId + "/resources",
                resourceReplan,
                ResourceReplan.class);

        return createdResource;
    }

    public ResourceReplan[] createResources(List<Person> p, String projectId) {
        List<ResourceReplan> resourceReplan = new ArrayList<ResourceReplan>();
        for (Person person:p) {
            ResourceReplan res=new ResourceReplan(person);
            resourceReplan.add(res);
        }
        ArrayResourceListReplan res=new ArrayResourceListReplan();
        res.setResourceReplan(resourceReplan);
        ResourceReplan[] createdResource = restTemplate.postForObject(
                replanUrl + "/projects/" + projectId + "/resources/create_n",
                resourceReplan,
                ResourceReplan[].class);

        return createdResource;
    }

    public FeatureReplan createRequirement(String requirement, String projectId) {
        FeatureReplan featureReplan = new FeatureReplan(requirement);

        FeatureReplan createdFeature = restTemplate.postForObject(
                replanUrl + "/projects/" + projectId + "/features/create_one",
                featureReplan,
                FeatureReplan.class);

        return createdFeature;
    }

    public FeatureReplan[] createRequirements(List<FeatureReplan> requirements, String projectId) {
        ArrayFeatureListReplan res=new ArrayFeatureListReplan();
        res.setFeatureReplan(requirements);
        FeatureReplan[] createdFeature = restTemplate.postForObject(
                replanUrl + "/projects/" + projectId + "/features/create_n",
                requirements,
                FeatureReplan[].class);

        return createdFeature;
    }

    public void deleteProject(String id) {
        restTemplate.delete(replanUrl + "/projects/" + id);
    }

    public SkillReplan createSkill(Skill s, String projectId) {
        SkillReplan skillReplan = new SkillReplan(s);
        SkillReplan createdReplan = restTemplate.postForObject(
                replanUrl + "/projects/" + projectId + "/skills",
                skillReplan,
                SkillReplan.class);
        return createdReplan;
    }

    public SkillReplan[] createSkills(Collection<Skill> skills, String projectId) {
        List<SkillReplan> skillsReplan=new ArrayList<SkillReplan>();
        for (Skill skill:skills) {
            skillsReplan.add(new SkillReplan(skill));
        }
        ArraySkillListReplan res=new ArraySkillListReplan();
        res.setSkillReplan(skillsReplan);
        SkillReplan[] createdReplan = restTemplate.postForObject(
                replanUrl + "/projects/" + projectId + "/skills/create_n",
                skillsReplan,
                SkillReplan[].class);
        return createdReplan;
    }

    public void addSkillsToPerson(String projectReplanId, Integer personId, List<SkillListReplan> skillListReplans) {
        List<Integer> st = new ArrayList<>();
        for (SkillListReplan sk : skillListReplans) st.add(sk.getSkill_id());
        System.out.println("Skills added to person: " + st);
        restTemplate.postForObject(
                replanUrl + "/projects/" + projectReplanId + "/resources/" + personId + "/skills",
                skillListReplans,
                Object.class);

    }

    public void addSkillsToRequirement(String projectReplanId, Integer reqId, List<SkillListReplan> skillListReplans) {
        List<Integer> st = new ArrayList<>();
        for (SkillListReplan sk : skillListReplans) st.add(sk.getSkill_id());
        System.out.println("Skills added to requirement: " + st);
        restTemplate.postForObject(
                replanUrl + "/projects/" + projectReplanId + "/features/" + reqId + "/skills",
                skillListReplans,
                Object.class);

    }

    public ReleaseReplan createRelease(String id) {
        ReleaseReplan releaseReplan = new ReleaseReplan("Release");
        ReleaseReplan createdRelease = restTemplate.postForObject(
                replanUrl + "/projects/" + id + "/releases",
                releaseReplan,
                ReleaseReplan.class);
        return createdRelease;
    }

    public void addResourcesToRelease(String projectReplanId, Integer releaseReplanId, List<ResourceListReplan> resourceReplanList) {
        restTemplate.postForObject(
                replanUrl + "/projects/" + projectReplanId + "/releases/" + releaseReplanId + "/resources",
                resourceReplanList,
                Object.class);
    }

    public void addFeaturesToRelease(String projectReplanId, Integer releaseReplanId, FeatureListReplan featureReplanList) {
        List<FeatureListReplan> skills = new ArrayList<FeatureListReplan>();
        skills.add(featureReplanList);
        restTemplate.postForObject(
                replanUrl + "/projects/" + projectReplanId + "/releases/" + releaseReplanId + "/features",
                skills,
                Object.class);
    }

    public Plan[] plan(String projectReplanId, Integer releaseReplanId) {
        return restTemplate.postForObject(
                replanUrl + "/projects/" + projectReplanId + "/releases/" + releaseReplanId + "/plan?multiple_solutions=true",
                null, Plan[].class);
    }

    public void deleteRelease(String projectReplanId, Integer releaseReplanId) {
        restTemplate.delete(replanUrl + "/projects/" + projectReplanId + "/releases/" + releaseReplanId);
    }

    public List<ResourceSkill> getResourceSkill(String projectReplanId, String resource_id) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<ResourceSkill>> response = restTemplate.exchange(
                replanUrl + "/projects/" + projectReplanId + "/resources/" + resource_id + "/skills",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ResourceSkill>>() {
                });
        List<ResourceSkill> aux = response.getBody();
        return aux;
    }

    public FeatureSkill getFeatureSkill(String projectReplanId, String feature_id) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<FeatureSkill> response = restTemplate.exchange(
                replanUrl + "/projects/" + projectReplanId + "/features/" + feature_id,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<FeatureSkill>() {
                });
        FeatureSkill aux = response.getBody();
        return aux;
    }

    public void modifyProject(Project p, Integer id_replan) {
    }
}
