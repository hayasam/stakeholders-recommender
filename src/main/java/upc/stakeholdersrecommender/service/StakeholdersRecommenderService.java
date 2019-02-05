package upc.stakeholdersrecommender.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import upc.stakeholdersrecommender.domain.OpenReqSchema;
import upc.stakeholdersrecommender.domain.PersonList;
import upc.stakeholdersrecommender.domain.RequirementList;
import upc.stakeholdersrecommender.domain.replan.*;
import upc.stakeholdersrecommender.entity.Person;
import upc.stakeholdersrecommender.entity.Project;
import upc.stakeholdersrecommender.entity.Requirement;
import upc.stakeholdersrecommender.entity.Skill;
import upc.stakeholdersrecommender.repository.PersonRepository;
import upc.stakeholdersrecommender.repository.RequirementRepository;
import upc.stakeholdersrecommender.repository.SkillRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class StakeholdersRecommenderService {

    @Autowired
    RequirementRepository requirementRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    SkillRepository skillRepository;

    @Autowired
    ReplanService replanService;

    public void addRequirements(RequirementList requirementList) {
        requirementRepository.saveAll(requirementList.getRequirements());
    }

    public Requirement getRequirement(String id) {
        return requirementRepository.getOne(id);
    }

    public void addPersons(PersonList personList) {
        personRepository.saveAll(personList.getPersons());
    }

    public Person getPerson(String id) {
        return personRepository.getOne(id);
    }

    public void recommend(OpenReqSchema request) {

        initializeData();

        //FIXME now we assume 1 project, N requirements and M persons, all belonging to project
        for (Project p : request.getProjects()) {

            //Create the project at Replan Service
            ProjectReplan projectReplan = replanService.createProject(p);

            List<Skill> skills = skillRepository.findAll();
            List<SkillReplan> skillReplanList = new ArrayList<>();
            for (Skill s : skills) {
                skillReplanList.add(replanService.createSkill(s, projectReplan.getId()));
            }

            List<ResourceReplan> resourceReplanList = new ArrayList<>();
            //For each person in the project, create a resource in the Replan Service
            for (Person person : request.getPersons()) {
                ResourceReplan resourceReplan = replanService.createResource(person, projectReplan.getId());
                resourceReplanList.add(resourceReplan);
                Person personWithSkills = personRepository.getOne(person.getUsername());
                replanService.addSkillsToPerson(projectReplan.getId(), resourceReplan.getId(), personWithSkills.getSkills(), skillReplanList);
            }

            List<FeatureReplan> featureReplanList = new ArrayList<>();
            //For each requirement in the project, create a feature in the Replan Service
            for (Requirement requirement : request.getRequirements()) {
                FeatureReplan featureReplan = replanService.createRequirement(requirement, projectReplan.getId());
                featureReplanList.add(featureReplan);
                Requirement requirementWithSkills = requirementRepository.getOne(requirement.getId());
                replanService.addSkillsToRequirement(projectReplan.getId(), featureReplan.getId(), requirementWithSkills.getSkills(), skillReplanList);
            }

            ReleaseReplan releaseReplan = replanService.createRelease(projectReplan.getId());
            replanService.addResourcesToRelease(projectReplan.getId(), releaseReplan.getId(), resourceReplanList);
            replanService.addFeaturesToRelease(projectReplan.getId(), releaseReplan.getId(), featureReplanList);

//            Object plan = replanService.plan(projectReplan.getId(), releaseReplan.getId());
            Plan plan = replanService.plan(projectReplan.getId(), releaseReplan.getId());

            replanService.deleteRelease(projectReplan.getId(), releaseReplan.getId());
            replanService.deleteProject(projectReplan.getId());

        }
    }

    private void initializeData() {
        Skill s1 = new Skill("Java");
        Skill s2 = new Skill("WebServices");
        Requirement r1 = new Requirement("R1");
        r1.setSkills(Arrays.asList(s1,s2));
        Person p1 = new Person("John");
        p1.setSkills(Arrays.asList(s1));
        Person p2 = new Person("Michael");
        p2.setSkills(Arrays.asList(s2));
        Person p3 = new Person("Alice");
        p3.setSkills(Arrays.asList(s1,s2));
        skillRepository.saveAll(Arrays.asList(s1,s2));
        personRepository.saveAll(Arrays.asList(p1,p2,p3));
        requirementRepository.saveAll(Arrays.asList(r1));
    }
}
