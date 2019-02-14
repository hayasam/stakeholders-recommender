package upc.stakeholdersrecommender.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import upc.stakeholdersrecommender.domain.OpenReqSchema;
import upc.stakeholdersrecommender.domain.PersonList;
import upc.stakeholdersrecommender.domain.RequirementList;
import upc.stakeholdersrecommender.domain.replan.*;
import upc.stakeholdersrecommender.entity.*;
import upc.stakeholdersrecommender.repository.PersonRepository;
import upc.stakeholdersrecommender.repository.RequirementRepository;
import upc.stakeholdersrecommender.repository.SkillRepository;

import java.util.*;

@Service
public class StakeholdersRecommenderService {

    @Autowired
    SkillRepository skillRepository;

    @Autowired
    ReplanService replanService;

    public void addRequirements(RequirementList requirementList) {
    }

    public void getRequirement(String id) {
    }

    public void addPersons(PersonList personList) {
    }

    public void getPerson(String id) {
    }

    public List<ReturnObject> recommend(OpenReqSchema request) {

        initializeSkills(request);
        List<ReturnObject> realreturn=new ArrayList<ReturnObject>();
        //FIXME now we assume 1 project, N requirements and M persons, all belonging to project
        for (Project p : request.getProjects()) {
            //Traductors from Replan Ids to stakeholder-recommender Ids
            HashMap<Integer,Requirement> requirementToFeature=new HashMap<Integer,Requirement>();
            //Create the project at Replan Service
            ProjectReplan projectReplan = replanService.createProject(p);

            List<Skill> skills = skillRepository.findAll();
            Map<String,SkillReplan> skillReplanMap= new HashMap<String,SkillReplan>();
            for (Skill s : skills) {
                SkillReplan aux=replanService.createSkill(s, projectReplan.getId());
                skillReplanMap.put(s.getName(),aux);
            }

            List<ResourceReplan> resourceReplanList = new ArrayList<>();
            //For each person in the project, create a resource in the Replan Service
            for (Person person : request.getPersons()) {
                ResourceReplan resourceReplan = replanService.createResource(person, projectReplan.getId());
                resourceReplanList.add(resourceReplan);
                replanService.addSkillsToPerson(projectReplan.getId(), resourceReplan.getId(), person.getSkills(), skillReplanMap);
            }

            List<FeatureReplan> featureReplanList = new ArrayList<>();
            //For each requirement in the project, create a feature in the Replan Service
            for (Requirement requirement : request.getRequirements()) {
                FeatureReplan featureReplan = replanService.createRequirement(requirement, projectReplan.getId());
                featureReplanList.add(featureReplan);
                requirementToFeature.put(featureReplan.getId(),requirement);
                replanService.addSkillsToRequirement(projectReplan.getId(), featureReplan.getId(), requirement.getSkills(), skillReplanMap);
            }
            ReleaseReplan releaseReplan = replanService.createRelease(projectReplan.getId());
            replanService.addResourcesToRelease(projectReplan.getId(), releaseReplan.getId(), resourceReplanList);
            replanService.addFeaturesToRelease(projectReplan.getId(), releaseReplan.getId(), featureReplanList);

//            Object plan = replanService.plan(projectReplan.getId(), releaseReplan.getId());
            Plan plan = replanService.plan(projectReplan.getId(), releaseReplan.getId());
            Map<String, Set<String>> output=plan.getRequirementStakeholder();
            realreturn.addAll(convert(output,requirementToFeature));
            replanService.deleteRelease(projectReplan.getId(), releaseReplan.getId());
            replanService.deleteProject(projectReplan.getId());

        }
        return realreturn;
    }

    private List<ReturnObject> convert(Map<String, Set<String>> output, HashMap<Integer,Requirement> requirementToFeature) {
        List<ReturnObject> toret=new ArrayList<ReturnObject>();
        for (String i:output.keySet()) {
            ReturnObject aux= new ReturnObject(i);
            List<String> helper= new ArrayList<String>();
            for (String s: output.get(i)) {
                helper.add(requirementToFeature.get(Integer.parseInt(s)).getId());
            }
            aux.setFeatureID(helper);
            toret.add(aux);
        }
        return toret;
    }

    // Just to put skills since they don't come in the json request yet, skills have to be manually put, replacing the fors if necessary
    private void initializeSkills(OpenReqSchema r) {
        Skill s1 = new Skill("Java");
        Skill s2 = new Skill("WebServices");
        List<Requirement> recsave=r.getRequirements();
        for (Requirement aux:recsave) aux.setSkills(Arrays.asList(s1,s2));
        List<Person> persave=r.getPersons();
        for (Person aux: persave) aux.setSkills(Arrays.asList(s1,s2));
        skillRepository.saveAll(Arrays.asList(s1,s2));
    }
}
