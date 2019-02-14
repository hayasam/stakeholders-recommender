package upc.stakeholdersrecommender.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import upc.stakeholdersrecommender.domain.replan.*;
import upc.stakeholdersrecommender.entity.Person;
import upc.stakeholdersrecommender.entity.Project;
import upc.stakeholdersrecommender.entity.Requirement;
import upc.stakeholdersrecommender.entity.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
                new ParameterizedTypeReference<Project>(){});
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

    public ResourceReplan createResource(Person p, Integer projectId) {
        ResourceReplan resourceReplan = new ResourceReplan(p);

        ResourceReplan createdResource = restTemplate.postForObject(
                replanUrl + "/projects/" + projectId + "/resources",
                resourceReplan,
                ResourceReplan.class);

        return createdResource;
    }

    public FeatureReplan createRequirement(Requirement requirement, Integer projectId) {
        FeatureReplan featureReplan = new FeatureReplan(requirement);

        FeatureReplan createdFeature = restTemplate.postForObject(
                replanUrl+ "/projects/" + projectId + "/features/create_one",
                featureReplan,
                FeatureReplan.class);

        return createdFeature;
    }

    public void deleteProject(Integer id) {
        restTemplate.delete(replanUrl + "/projects/" + id);
    }

    public SkillReplan createSkill(Skill s, Integer projectId) {
        SkillReplan skillReplan = new SkillReplan(s);

        SkillReplan createdReplan = restTemplate.postForObject(
                replanUrl + "/projects/" + projectId + "/skills",
                skillReplan,
                SkillReplan.class);

        return createdReplan;
    }

    public void addSkillsToPerson(Integer projectReplanId, Integer personId, List<Skill> personWithSkills, Map<String,SkillReplan> skillReplanMap) {
        List<SkillListReplan> skillListReplans = new ArrayList<>();
        for (int i = 0; i < personWithSkills.size(); ++i) {
            if (skillReplanMap.containsKey(personWithSkills.get(i).getName()))
                    skillListReplans.add(new SkillListReplan(skillReplanMap.get(personWithSkills.get(i).getName()).getId()));
            }

        restTemplate.postForObject(
                replanUrl + "/projects/" + projectReplanId + "/resources/" + personId + "/skills",
                skillListReplans,
                Object.class);

    }

    public void addSkillsToRequirement(Integer projectReplanId, Integer reqId, List<Skill> requirementWithSkills, Map<String,SkillReplan> skillReplanMap) {
        List<SkillListReplan> skillListReplans = new ArrayList<>();
        for (int i = 0; i < requirementWithSkills.size(); ++i) {
            if (skillReplanMap.containsKey(requirementWithSkills.get(i).getName()))
                skillListReplans.add(new SkillListReplan(skillReplanMap.get(requirementWithSkills.get(i).getName()).getId()));
        }
        restTemplate.postForObject(
                replanUrl + "/projects/" + projectReplanId + "/features/" + reqId + "/skills",
                skillListReplans,
                Object.class);

    }

    public ReleaseReplan createRelease(Integer id) {
        ReleaseReplan releaseReplan = new ReleaseReplan("Release");
        ReleaseReplan createdRelease = restTemplate.postForObject(
                replanUrl + "/projects/" + id + "/releases",
                releaseReplan,
                ReleaseReplan.class);
        return createdRelease;
    }

    public void addResourcesToRelease(Integer projectReplanId, Integer releaseReplanId, List<ResourceReplan> resourceReplanList) {
        List<ResourceListReplan> resourceListReplans = new ArrayList<>();
        for (ResourceReplan resource : resourceReplanList) resourceListReplans.add(new ResourceListReplan(resource.getId()));
        restTemplate.postForObject(
                replanUrl  + "/projects/" + projectReplanId + "/releases/" + releaseReplanId + "/resources",
                resourceListReplans,
                Object.class);
    }

    public void addFeaturesToRelease(Integer projectReplanId, Integer releaseReplanId, List<FeatureReplan> featureReplanList) {
        List<FeatureListReplan> resourceListReplans = new ArrayList<>();
        for (FeatureReplan featureReplan : featureReplanList) resourceListReplans.add(new FeatureListReplan(featureReplan.getId()));
        restTemplate.postForObject(
                replanUrl  + "/projects/" + projectReplanId + "/releases/" + releaseReplanId + "/features",
                resourceListReplans,
                Object.class);
    }

    public Plan plan(Integer projectReplanId, Integer releaseReplanId) {
        Plan[] plans=restTemplate.postForObject(
                replanUrl + "/projects/" + projectReplanId + "/releases/" + releaseReplanId + "/plan?multiple_solutions=false",
                null,Plan[].class);
        System.out.println(plans.length);
        return plans[0];
    }

    public void deleteRelease(Integer projectReplanId, Integer releaseReplanId) {
        restTemplate.delete(replanUrl + "/projects/" + projectReplanId + "/releases/" + releaseReplanId);
    }
}
