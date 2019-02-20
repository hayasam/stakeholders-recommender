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
    RequirementSkillsRepository RequirementSkillsRepository;


    @Autowired
    ReplanService replanService;

    public List<Responsible> recommend(RecommendSchema request) {

        // Recibo 1 feature, 1 requirement, he de dar lista de gente
        Project p=request.getProject();
        Requirement r=request.getRequirement();
        Integer project_replanID=ProjectToPReplanRepository.getOne(p.getId()).getID_Replan();
        Integer requirement_replanID=RequirementToFeatureRepository.getOne(r.getId()).getID_Replan();
        ReleaseReplan release=replanService.createRelease(project_replanID);

        replanService.addFeaturesToRelease(project_replanID,release.getId(),new FeatureListReplan(requirement_replanID));
        List<ResourceListReplan> reslist=new ArrayList<ResourceListReplan>();
        for (PersonToPReplan pers: PersonToPReplanRepository.findByProjectIdQuery(project_replanID)) reslist.add(new ResourceListReplan(pers.getId_replan()));
        replanService.addResourcesToRelease(project_replanID,release.getId(),reslist);
        Plan[] plan=replanService.plan(project_replanID,release.getId());

        Map<String,Set<String>> output=new HashMap<String,Set<String>>();
        for (Plan auxplan:plan) {
            Map<String,Set<String>> aux=Parse(auxplan);
            for (String s:aux.keySet()) {
                if (output.containsKey(s)) {
                    Set<String> value=output.get(s);
                    value.addAll(aux.get(s));
                    output.replace(s,value);
                }
                else output.put(s,aux.get(s));
            }
        }
        List<Responsible> returnobject=new ArrayList<Responsible>();
        for (String s:output.keySet()) {
            Set<String> inRetty=Reject(request.getUser().getEmail(),Translate(output.get(s)),s);
            Responsible retty=new Responsible(s,inRetty);
            returnobject.add(retty);
        }
        replanService.deleteRelease(project_replanID,release.getId());
        return returnobject;
    }


    public void recommend_reject(RejectSchema request) {
        String personRejected=request.getRejected().getEmail();
        if (RejectedPersonRepository.existsById(request.getUser().getEmail())) {
            RejectedPerson rejected = RejectedPersonRepository.getOne(request.getUser().getEmail());
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
            RejectedPerson reject= new RejectedPerson(request.getUser().getEmail());
            HashMap<String,Set<String>> aux=new HashMap<String,Set<String>>();
            Set<String> auxset=new HashSet<String>();
            auxset.add(request.getRequirement().getId());
            aux.put(personRejected,auxset);
            reject.setDeleted(aux);
            RejectedPersonRepository.save(reject);
        }
    }


    public void addBatch(BatchSchema request) {
        List<Pair> pairs=new ArrayList<Pair>();
        for (Project p : request.getProjects()) {
            Integer id = instanciateProject(p);
            SkillReplan skill = replanService.createSkill(new Skill("Stuff"), Integer.parseInt(p.getId()));
            RequirementSkills req=new RequirementSkills(skill.getId(),id);
            RequirementSkillsRepository.save(req);
            Pair aux=new Pair(id,skill.getId());
            pairs.add(new Pair(id,skill.getId()));
        }
        //Instanciate people
        for (Person person : request.getPersons()) {
            for (Pair p:pairs) instanciateResources(person, p.first, p.second);
        }
        //Instanciate Requirements
        for (Requirement requirement : request.getRequirements()) {
            for (Pair p:pairs) instanciateFeatures(requirement, p.first, p.second);
        }
    }




    private void instanciateResources(Person person, Integer id,Integer skill) {
        if (PersonToPReplanRepository.findById(new PersonId(id,person.getEmail()))==null) {
            System.out.println(person.getEmail());
            ResourceReplan resourceReplan = replanService.createResource(person, id);
            PersonToPReplan personTrad = new PersonToPReplan(new PersonId(id,person.getEmail()));
            personTrad.setId_replan(resourceReplan.getId());
            personTrad.setProjectIdQuery(id);
            PersonToPReplanRepository.save(personTrad);

            //List<SkillReplan> skill = computeSkillsPerson(person);
            // TODO Add skills to person in replan
            replanService.addSkillsToPerson(id,resourceReplan.getId(),new SkillListReplan(skill));
        } else {
        }

    }
    private void instanciateFeatures(Requirement requirement, Integer id, Integer skill) {
        if (!RequirementToFeatureRepository.existsById(requirement.getId())) {
            FeatureReplan featureReplan = replanService.createRequirement(requirement, id);
            RequirementToFeature requirementTrad = new RequirementToFeature(requirement.getId());
            requirementTrad.setID_Replan(featureReplan.getId());
            requirementTrad.setProjectIdQuery(id);
            RequirementToFeatureRepository.save(requirementTrad);

            //  List<SkillReplan> skill = computeSkillsRequirement(requirement);
            // TODO Add skills to requirements in replan
            replanService.addSkillsToRequirement(id,featureReplan.getId(),new SkillListReplan(skill));
        }
    }

    private Integer instanciateProject(Project p) {
        Integer id=null;
        if (ProjectToPReplanRepository.existsById(p.getId())) {
            id=ProjectToPReplanRepository.getOne(p.getId()).getID_Replan();
            replanService.deleteProject(id);
            DeleteRelated(id);
        }
        ProjectReplan projectReplan = replanService.createProject(p);
        id=projectReplan.getId();
        ProjectToPReplan projectTrad = new ProjectToPReplan(p.getId());
        projectTrad.setID_Replan(projectReplan.getId());
        ProjectToPReplanRepository.save(projectTrad);

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

    private void DeleteRelated(Integer id) {
        RequirementSkillsRepository.deleteByProjectIdQuery(id);
        PersonToPReplanRepository.deleteByProjectIdQuery(id);
        RequirementToFeatureRepository.deleteByProjectIdQuery(id);
    }


    public void purge() {
        ProjectToPReplanRepository.deleteAll();
        PersonToPReplanRepository.deleteAll();
        RequirementToFeatureRepository.deleteAll();
        RejectedPersonRepository.deleteAll();
    }

    private class Pair {
        private Integer first;
        private Integer second;


        public Pair(Integer first,Integer second) {
            this.first=first;
            this.second=second;
        }
        public Integer getFirst() {
            return first;
        }

        public void setFirst(Integer first) {
            this.first = first;
        }

        public Integer getSecond() {
            return second;
        }

        public void setSecond(Integer second) {
            this.second = second;
        }
    }
}
