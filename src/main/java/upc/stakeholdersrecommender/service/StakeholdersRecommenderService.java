package upc.stakeholdersrecommender.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import upc.stakeholdersrecommender.domain.*;
import upc.stakeholdersrecommender.domain.Schemas.BatchSchema;
import upc.stakeholdersrecommender.domain.Schemas.RecommendSchema;
import upc.stakeholdersrecommender.domain.replan.*;
import upc.stakeholdersrecommender.entity.*;
import upc.stakeholdersrecommender.repository.*;

import java.io.IOException;
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
    RequirementSkillsRepository RequirementSkillsRepository;


    @Autowired
    ReplanService replanService;

    public List<Responsible> recommend(RecommendSchema request) {

        // Recibo 1 feature, 1 requirement, he de dar lista de gente
        Project p = request.getProject();
        Requirement r = request.getRequirement();
        Integer project_replanID = ProjectToPReplanRepository.getOne(p.getId()).getIdReplan();
        Integer requirement_replanID = RequirementToFeatureRepository.findById(new RequirementId(project_replanID,Integer.parseInt(r.getId()))).getID_Replan();
        ReleaseReplan release = replanService.createRelease(project_replanID);
        Integer releaseId=release.getId();
        String user = request.getUser().getUsername();

        replanService.addFeaturesToRelease(project_replanID, release.getId(), new FeatureListReplan(requirement_replanID));
        List<ResourceListReplan> reslist = new ArrayList<ResourceListReplan>();
        for (PersonToPReplan pers : PersonToPReplanRepository.findByProjectIdQuery(project_replanID))
            reslist.add(new ResourceListReplan(pers.getIdReplan()));
        replanService.addResourcesToRelease(project_replanID, releaseId, reslist);
        Plan[] plan = replanService.plan(project_replanID,releaseId);

        Map<Integer, Set<String>> output = fusePlans(plan);
        List<Responsible> returnobject = createOutput(user, output);
        replanService.deleteRelease(project_replanID, releaseId);
        return returnobject;
    }

    public void purge() {
        ProjectToPReplanRepository.deleteAll();
        PersonToPReplanRepository.deleteAll();
        RequirementToFeatureRepository.deleteAll();
        RejectedPersonRepository.deleteAll();
    }

    public void recommend_reject(String rejectedId, String userId, String requirementId) {
        if (RejectedPersonRepository.existsById(userId)) {
            RejectedPerson rejected = RejectedPersonRepository.getOne(userId);
            if (rejected.getDeleted().containsKey(rejectedId)) {
                rejected.getDeleted().get(rejectedId).add(requirementId);
            } else {
                Set<String> aux = new HashSet<String>();
                aux.add(requirementId);
                rejected.getDeleted().put(rejectedId, aux);
            }
            RejectedPersonRepository.save(rejected);
        } else {
            RejectedPerson reject = new RejectedPerson(userId);
            HashMap<String, Set<String>> aux = new HashMap<String, Set<String>>();
            Set<String> auxset = new HashSet<String>();
            auxset.add(requirementId);
            aux.put(rejectedId, auxset);
            reject.setDeleted(aux);
            RejectedPersonRepository.save(reject);
        }
    }


    public void addBatch(BatchSchema request) throws IOException {
        List<SkillListReplan> allSkills=new ArrayList<SkillListReplan>();
        List<Integer> projectIds=new ArrayList<Integer>();
        for (Project p : request.getProjects()) {
            Integer id = instanciateProject(p);
            projectIds.add(id);
            for (Integer requirement : p.getSpecifiedRequirements()) {
                List<SkillListReplan> skills=computeSkillsRequirement(requirement,id);
                allSkills.addAll(skills);
                instanciateFeatures(requirement, id,skills);

            }
        }
        //Instanciate people
        for (Person person : request.getPersons()) {
            for (Integer id:projectIds) instanciateResources(person, id, allSkills);
        }
    }


    private List<Responsible> createOutput(String user, Map<Integer, Set<String>> output) {
        List<Responsible> returnobject = new ArrayList<Responsible>();
        for (Integer s : output.keySet()) {
            String username = PersonToPReplanRepository.findByIdReplan(s).getId().getPersonId();
            Set<String> inRetty = reject(user, translate(output.get(s)), username);
            for (String req: inRetty) {
                returnobject.add(new Responsible(username,Integer.parseInt(req)));
            }
        }
        return returnobject;
    }

    private Map<Integer, Set<String>> fusePlans(Plan[] plan) {
        Map<Integer, Set<String>> output = new HashMap<Integer, Set<String>>();
        for (int i = 0; i < plan.length && i < 10; i++) {
            Plan auxplan = plan[i];
            Map<Integer, Set<String>> aux = parse(auxplan);
            for (Integer s : aux.keySet()) {
                if (output.containsKey(s)) {
                    Set<String> value = output.get(s);
                    value.addAll(aux.get(s));
                    output.replace(s, value);
                } else output.put(s, aux.get(s));
            }
        }
        return output;
    }

    private void instanciateResources(Person person, Integer id, List<SkillListReplan> skills) {
        if (PersonToPReplanRepository.findById(new PersonId(id, person.getUsername())) == null) {
            ResourceReplan resourceReplan = replanService.createResource(person, id);
            PersonToPReplan personTrad = new PersonToPReplan(new PersonId(id, person.getUsername()));
            personTrad.setIdReplan(resourceReplan.getId());
            personTrad.setProjectIdQuery(id);
            PersonToPReplanRepository.save(personTrad);

            // TODO Add skills to person in replan
            List<SkillListReplan> toAssign=randomSkills(skills);
            replanService.addSkillsToPerson(id, resourceReplan.getId(),toAssign);
        } else {
        }

    }

    private List<SkillListReplan> randomSkills(List<SkillListReplan> skills) {
        List<SkillListReplan> toret= new ArrayList<SkillListReplan>();
        for (SkillListReplan skill:skills) {
            Random rand = new Random();
            if(rand.nextBoolean()) {
                SkillListReplan auxil=new SkillListReplan();
                auxil.setWeight(rand.nextDouble());
                auxil.setSkill_id(skill.getSkill_id());
                toret.add(auxil);
            }
        }
        return toret;
    }

    private void instanciateFeatures(Integer requirement, Integer id, List<SkillListReplan> skills) {
        if (RequirementToFeatureRepository.findById(new RequirementId(id,requirement))==null) {
            FeatureReplan featureReplan = replanService.createRequirement(requirement, id);
            RequirementToFeature requirementTrad = new RequirementToFeature(new RequirementId(id,requirement));
            requirementTrad.setID_Replan(featureReplan.getId());
            requirementTrad.setProjectIdQuery(id);
            RequirementToFeatureRepository.save(requirementTrad);

            // TODO Add skills to requirements in replan
            replanService.addSkillsToRequirement(id, featureReplan.getId(), skills);
        }
    }

    private Integer instanciateProject(Project p) {
        Integer id = null;
        if (ProjectToPReplanRepository.existsById(p.getId())) {
            id = ProjectToPReplanRepository.getOne(p.getId()).getIdReplan();
            replanService.deleteProject(id);
            deleteRelated(id);
        }
        ProjectReplan projectReplan = replanService.createProject(p);
        id = projectReplan.getId();
        ProjectToPReplan projectTrad = new ProjectToPReplan(p.getId());
        projectTrad.setIdReplan(projectReplan.getId());
        ProjectToPReplanRepository.save(projectTrad);

        return id;
    }

    private List<SkillListReplan>  computeSkillsRequirement(Integer requirement,Integer id) {
        Skill auxiliar=new Skill("Stuff",1.0);
        SkillReplan skill=replanService.createSkill(auxiliar,id);
        List<SkillListReplan> toret=new ArrayList<SkillListReplan>();
        toret.add(new SkillListReplan(skill));
        return toret;
    }

    private List<SkillListReplan>  computeSkillsPerson(Person person, Integer id) {
        Skill auxiliar=new Skill("Stuff",1.0);
        SkillReplan skill=replanService.createSkill(auxiliar,id);
        List<SkillListReplan> toret=new ArrayList<SkillListReplan>();
        toret.add(new SkillListReplan(skill));
        return toret;
    }

    private Map<Integer, Set<String>> parse(Plan plan) {
        List<ResourceReplan> resources = plan.getResources();
        Map<Integer, Set<String>> toret = new HashMap<>();
        for (ResourceReplan res : resources) toret.put(res.getId(), res.getFeaturesWorkedOn());
        return toret;
    }

    private Set<String> reject(String rejector, Set<String> stuff, String person) {
        if (RejectedPersonRepository.existsById(rejector)) {
            RejectedPerson rej = RejectedPersonRepository.getOne(rejector);
            HashMap<String, Set<String>> rejectedRequirements = rej.getDeleted();
            if (rejectedRequirements.containsKey(person)) {
                stuff.removeAll(rejectedRequirements.get(person));
            }
        }
        return stuff;
    }

    private Set<String> translate(Set<String> id_replan) {
        Set<String> aux = new HashSet<String>();
        for (String s : id_replan) {
            aux.add(RequirementToFeatureRepository.findByIdReplan(Integer.parseInt(s)).getID().getRequirementId().toString());
        }
        return aux;
    }

    private void deleteRelated(Integer id) {
        RequirementSkillsRepository.deleteByProjectIdQuery(id);
        PersonToPReplanRepository.deleteByProjectIdQuery(id);
        RequirementToFeatureRepository.deleteByProjectIdQuery(id);
    }


    private List<SkillListReplan> computeAllSkills(Integer id) {
        Skill auxiliar=new Skill("Stuff",1.0);
        SkillReplan skill=replanService.createSkill(auxiliar,id);
        List<SkillListReplan> toret=new ArrayList<SkillListReplan>();
        toret.add(new SkillListReplan(skill));
        return toret;
    }

}
