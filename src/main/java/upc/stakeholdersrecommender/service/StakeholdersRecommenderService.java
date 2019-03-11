package upc.stakeholdersrecommender.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import upc.stakeholdersrecommender.domain.*;
import upc.stakeholdersrecommender.domain.Schemas.BatchSchema;
import upc.stakeholdersrecommender.domain.Schemas.ExtractTest;
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
        String p = request.getProject();
        String r = request.getRequirement();
        String project_replanID = ProjectToPReplanRepository.getOne(p).getIdReplan().toString();
        String requirement_replanID = RequirementToFeatureRepository.findById(new RequirementId(project_replanID,r)).getID_Replan();
        ReleaseReplan release = replanService.createRelease(project_replanID);
        Integer releaseId=release.getId();
        String user = request.getUser();

        replanService.addFeaturesToRelease(project_replanID, release.getId(), new FeatureListReplan(requirement_replanID));
        List<ResourceListReplan> reslist = new ArrayList<ResourceListReplan>();
        for (PersonToPReplan pers : PersonToPReplanRepository.findByProjectIdQuery(project_replanID))
            reslist.add(new ResourceListReplan(pers.getIdReplan()));
        replanService.addResourcesToRelease(project_replanID, releaseId, reslist);
        Plan[] plan = replanService.plan(project_replanID,releaseId);

        Map<String, Set<String>> output = fusePlans(plan);
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
        List<String> projectIds=new ArrayList<String>();
        for (Project p : request.getProjects()) {
            String id = instanciateProject(p);
            projectIds.add(id);
            for (String requirement : p.getSpecifiedRequirements()) {
                List<SkillListReplan> skills=computeSkillsRequirement(requirement,id);
                allSkills.addAll(skills);
                instanciateFeatures(requirement, id,skills);

            }
        }
        //Instanciate people
        for (Person person : request.getPersons()) {
            for (String id:projectIds) instanciateResources(person, id, allSkills);
        }
    }


    private List<Responsible> createOutput(String user, Map<String, Set<String>> output) {
        List<Responsible> returnobject = new ArrayList<Responsible>();
        for (String s : output.keySet()) {
            String username = PersonToPReplanRepository.findByIdReplan(s).getId().getPersonId();
            Set<String> inRetty = reject(user, translate(output.get(s)), username);
            for (String req: inRetty) {
                returnobject.add(new Responsible(username,Integer.parseInt(req)));
            }
        }
        return returnobject;
    }

    private Map<String, Set<String>> fusePlans(Plan[] plan) {
        Map<String, Set<String>> output = new HashMap<String, Set<String>>();
        for (int i = 0; i < plan.length && i < 10; i++) {
            Plan auxplan = plan[i];
            Map<String, Set<String>> aux = parse(auxplan);
            for (String s : aux.keySet()) {
                if (output.containsKey(s)) {
                    Set<String> value = output.get(s);
                    value.addAll(aux.get(s));
                    output.replace(s, value);
                } else output.put(s, aux.get(s));
            }
        }
        return output;
    }

    private void instanciateResources(Person person, String id, List<SkillListReplan> skills) {
        if (PersonToPReplanRepository.findById(new PersonId(id, person.getUsername())) == null) {
            ResourceReplan resourceReplan = replanService.createResource(person, id);
            PersonToPReplan personTrad = new PersonToPReplan(new PersonId(id, person.getUsername()));
            personTrad.setIdReplan(resourceReplan.getId().toString());
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

    private void instanciateFeatures(String requirement, String id, List<SkillListReplan> skills) {
        if (RequirementToFeatureRepository.findById(new RequirementId(id,requirement))==null) {
            FeatureReplan featureReplan = replanService.createRequirement(requirement, id);
            RequirementToFeature requirementTrad = new RequirementToFeature(new RequirementId(id,requirement));
            requirementTrad.setID_Replan(featureReplan.getId().toString());
            requirementTrad.setProjectIdQuery(id);
            RequirementToFeatureRepository.save(requirementTrad);

            // TODO Add skills to requirements in replan
            replanService.addSkillsToRequirement(id, featureReplan.getId(), skills);
        }
    }

    private String instanciateProject(Project p) {
        String id = null;
        if (ProjectToPReplanRepository.existsById(p.getId())) {
            id = ProjectToPReplanRepository.getOne(p.getId()).getIdReplan().toString();
            replanService.deleteProject(id);
            deleteRelated(id);
        }
        ProjectReplan projectReplan = replanService.createProject(p);
        id = projectReplan.getId().toString();
        ProjectToPReplan projectTrad = new ProjectToPReplan(p.getId());
        projectTrad.setIdReplan(projectReplan.getId());
        ProjectToPReplanRepository.save(projectTrad);

        return id;
    }

    private List<SkillListReplan>  computeSkillsRequirement(String requirement,String id) {
        Skill auxiliar=new Skill("Stuff",1.0);
        SkillReplan skill=replanService.createSkill(auxiliar,id);
        List<SkillListReplan> toret=new ArrayList<SkillListReplan>();
        toret.add(new SkillListReplan(skill));
        return toret;
    }

    private List<SkillListReplan>  computeSkillsPerson(String person, String id) {
        Skill auxiliar=new Skill("Stuff",1.0);
        SkillReplan skill=replanService.createSkill(auxiliar,id);
        List<SkillListReplan> toret=new ArrayList<SkillListReplan>();
        toret.add(new SkillListReplan(skill));
        return toret;
    }

    private Map<String, Set<String>> parse(Plan plan) {
        List<ResourceReplan> resources = plan.getResources();
        Map<String, Set<String>> toret = new HashMap<>();
        for (ResourceReplan res : resources) toret.put(res.getId().toString(), res.getFeaturesWorkedOn());
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
            aux.add(RequirementToFeatureRepository.findByIdReplan(s).getID().getRequirementId());
        }
        return aux;
    }

    private void deleteRelated(String id) {
        RequirementSkillsRepository.deleteByProjectIdQuery(id);
        PersonToPReplanRepository.deleteByProjectIdQuery(id);
        RequirementToFeatureRepository.deleteByProjectIdQuery(id);
    }


    private List<SkillListReplan> computeAllSkills(String id) {
        Skill auxiliar=new Skill("Stuff",1.0);
        SkillReplan skill=replanService.createSkill(auxiliar,id);
        List<SkillListReplan> toret=new ArrayList<SkillListReplan>();
        toret.add(new SkillListReplan(skill));
        return toret;
    }

    public void extract(ExtractTest request) throws IOException {
        KeywordExtractor extractor=new KeywordExtractor();
        List<Map<String,Double>> res= extractor.extractKeywords(request.getCorpus());
        Integer i=0;
        for (Map<String,Double> map:res) {
            System.out.println("------------------------------");
            System.out.println("Document Number "+i);
            System.out.println("------------------------------");
            for (String s:map.keySet()) {
                if (map.get(s)>3)
                System.out.println(s+"  "+map.get(s));
            }
            ++i;
        }
    }
}
