package upc.stakeholdersrecommender.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import upc.stakeholdersrecommender.domain.*;
import upc.stakeholdersrecommender.domain.Schemas.BatchSchema;
import upc.stakeholdersrecommender.domain.Schemas.RejectSchema;
import upc.stakeholdersrecommender.domain.Schemas.RecommendSchema;
import upc.stakeholdersrecommender.domain.replan.*;
import upc.stakeholdersrecommender.entity.*;
import upc.stakeholdersrecommender.repository.*;

import java.util.*;

@Service
public class StakeholdersRecommenderService {

    @Autowired
    PersonToPReplanRepository PersonToPReplanRepository;
    @Autowired
    ProjectToPReplanRepository ProjectToPReplanRepository;
    @Autowired
    RequirementToFeatureRepository RequirementToFeatureRepository;
    @Autowired
    RejectedPersonRepository RejectedPersonRepository;



    @Autowired
    ReplanService replanService;

    public List<ReturnObject> recommend(RecommendSchema request) {

        // Recibo 1 feature, 1 requirement, he de dar lista de gente
        Project p=request.getProject();
        Requirement r=request.getRequirement();
        Integer project_replanID=ProjectToPReplanRepository.getOne(p.getId()).getID_Replan();
        Integer requirement_replanID=RequirementToFeatureRepository.getOne(r.getId()).getID_Replan();
        ReleaseReplan release=replanService.createRelease(project_replanID);

        replanService.addFeaturesToRelease(project_replanID,release.getId(),new FeatureListReplan(requirement_replanID));
        List<ResourceListReplan> reslist=new ArrayList<ResourceListReplan>();
        for (PersonToPReplan pers:PersonToPReplanRepository.findAll()) reslist.add(new ResourceListReplan(pers.getID_Replan()));
        replanService.addResourcesToRelease(project_replanID,release.getId(),reslist);
        Plan plan=replanService.plan(project_replanID,release.getId());
        Map<String,Set<String>> output=Parse(plan);
        List<ReturnObject> returnobject=new ArrayList<ReturnObject>();
        for (String s:output.keySet()) {
            Set<String> inRetty=Reject(request.getUser().getUsername(),Translate(output.get(s)),s);
            ReturnObject retty=new ReturnObject(s);
            retty.setRequirement(inRetty);
            returnobject.add(retty);
        }
        replanService.deleteRelease(project_replanID,release.getId());
        return returnobject;
    }


    public void recommend_reject(RejectSchema request) {
        String personRejected=request.getRejected().getUsername();
        if (RejectedPersonRepository.existsById(request.getUser().getUsername())) {
            RejectedPerson rejected = RejectedPersonRepository.getOne(request.getUser().getUsername());
            if (rejected.getDeleted().containsKey(personRejected)) {
                rejected.getDeleted().get(personRejected).add(request.getRequirement().getId());
            }
            else  {
                Set<String> aux=new HashSet<String>();
                aux.add(request.getRequirement().getId());
                rejected.getDeleted().put(personRejected,aux);
            }
        }
        else {
            RejectedPerson reject= new RejectedPerson(request.getUser().getUsername());
            HashMap<String,Set<String>> aux=new HashMap<String,Set<String>>();
            Set<String> auxset=new HashSet<String>();
            auxset.add(request.getRequirement().getId());
            aux.put(personRejected,auxset);
            reject.setDeleted(aux);
            RejectedPersonRepository.save(reject);
        }
    }


    public void addBatch(BatchSchema request) {
        for (Project p : request.getProjects()) {
            SkillReplan skill = replanService.createSkill(new Skill("Stuff"), Integer.parseInt(p.getId()));
            Integer id=instanciateProject(p);
            //Instanciate people
            for (Person person : request.getPersons()) { instanciateResources(person, id, skill); }
            //Instanciate Requirements
            for (Requirement requirement : request.getRequirements()) { instanciateFeatures(requirement, id, skill); }
            }
        }



    private void instanciateResources(Person person, Integer id,SkillReplan skill) {
        if (!PersonToPReplanRepository.existsById(person.getUsername())) {
            ResourceReplan resourceReplan = replanService.createResource(person, id);
            PersonToPReplan personTrad = new PersonToPReplan(person.getUsername());
            personTrad.setID_Replan(resourceReplan.getId());
            PersonToPReplanRepository.save(personTrad);

            //List<SkillReplan> skill = computeSkillsPerson(person);
            // TODO Add skills to person in replan
            replanService.addSkillsToPerson(id,resourceReplan.getId(),new SkillListReplan(skill.getId()));
        } else {
            replanService.modifyResource(person,PersonToPReplanRepository.getOne(person.getUsername()).getID_Replan(),id);
        }

    }
    private void instanciateFeatures(Requirement requirement, Integer id, SkillReplan skill) {
        if (!RequirementToFeatureRepository.existsById(requirement.getId())) {
            FeatureReplan featureReplan = replanService.createRequirement(requirement, id);
            RequirementToFeature requirementTrad = new RequirementToFeature(requirement.getId());
            requirementTrad.setID_Replan(featureReplan.getId());
            RequirementToFeatureRepository.save(requirementTrad);

            //  List<SkillReplan> skill = computeSkillsRequirement(requirement);
            // TODO Add skills to requirements in replan
            replanService.addSkillsToRequirement(id,featureReplan.getId(),new SkillListReplan(skill.getId()));
        } else {
            replanService.modifyRequirement(requirement, RequirementToFeatureRepository.getOne(requirement.getId()).getID_Replan(),id);
        }

    }

    private Integer instanciateProject(Project p) {
        Integer id=null;
        if (!ProjectToPReplanRepository.existsById(p.getId())) {
            ProjectReplan projectReplan = replanService.createProject(p);
            id=projectReplan.getId();
            ProjectToPReplan projectTrad = new ProjectToPReplan(p.getId());
            projectTrad.setID_Replan(projectReplan.getId());
            ProjectToPReplanRepository.save(projectTrad);
        }
        else {
            id=ProjectToPReplanRepository.getOne(p.getId()).getID_Replan();
            replanService.modifyProject(p,id);
        }
        return id;
    }


    private List<SkillReplan> computeSkillsRequirement(Requirement requirement) {
        return Arrays.asList(new SkillReplan(new Skill("Stuff")));
    }

    private List<SkillReplan> computeSkillsPerson(Person person) {
        return Arrays.asList(new SkillReplan(new Skill("Stuff")));
    }

    private Map<String,Set<String>> Parse(Plan plan) {
        List<ResourceReplan> resources=plan.getResources();
        Map<String,Set<String>> toret=new HashMap<>();
        for (ResourceReplan res:resources) toret.put(res.getName(),res.getFeaturesWorkedOn());
        return toret;
    }
    private Set<String>  Reject(String rejector,Set<String> stuff, String person) {
        if (RejectedPersonRepository.existsById(rejector)) {
            RejectedPerson rej = RejectedPersonRepository.getOne(rejector);
            HashMap<String, Set<String>> rejectedRequirements = rej.getDeleted();
            if (rejectedRequirements.containsKey(person)) {
                stuff.removeAll(rejectedRequirements.get(person));
            }
        }
        return stuff;
    }

    private Set<String> Translate(Set<String> id_replan) {
        Set<String> aux= new HashSet<String>();
        for (String s: id_replan) {
            aux.add(RequirementToFeatureRepository.findByIdReplan(Integer.parseInt(s)).getID());
        }
        return aux;
    }

    public void purge() {
        ProjectToPReplanRepository.deleteAll();
        PersonToPReplanRepository.deleteAll();
        RequirementToFeatureRepository.deleteAll();
        RejectedPersonRepository.deleteAll();

    }
}
