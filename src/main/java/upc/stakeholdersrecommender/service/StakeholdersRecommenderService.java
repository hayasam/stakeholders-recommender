package upc.stakeholdersrecommender.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import upc.stakeholdersrecommender.domain.*;
import upc.stakeholdersrecommender.domain.Schemas.*;
import upc.stakeholdersrecommender.domain.keywords.RAKEKeywordExtractor;
import upc.stakeholdersrecommender.domain.keywords.TFIDFKeywordExtractor;
import upc.stakeholdersrecommender.domain.replan.*;
import upc.stakeholdersrecommender.entity.*;
import upc.stakeholdersrecommender.repository.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Double.max;

@Service
public class StakeholdersRecommenderService {

    @Autowired
    private PersonToPReplanRepository PersonToPReplanRepository;
    @Autowired
    private ProjectToPReplanRepository ProjectToPReplanRepository;
    @Autowired
    private RequirementToFeatureRepository RequirementToFeatureRepository;
    @Autowired
    private RejectedPersonRepository RejectedPersonRepository;
    @Autowired
    private RequirementSkillsRepository RequirementSkillsRepository;
    @Autowired
    private ReplanService replanService;
    @Autowired
    private EffortRepository EffortRepository;


    public List<RecommendReturnSchema> recommend(RecommendSchema request, int k, Boolean projectSpecific) throws Exception {
        String p = request.getProject();
        String r = request.getRequirement();
        String project_replanID = ProjectToPReplanRepository.getOne(p).getIdReplan().toString();
        String requirement_replanID = "";
        if (RequirementToFeatureRepository.findById(new RequirementId(project_replanID, r)) != null) {
            requirement_replanID = RequirementToFeatureRepository.findById(new RequirementId(project_replanID, r)).getID_Replan();
        } else throw new Exception();
        ReleaseReplan release = replanService.createRelease(project_replanID);
        Integer releaseId = release.getId();
        String user = request.getUser();

        FeatureSkill featureSkills = replanService.getFeatureSkill(project_replanID, requirement_replanID);
        replanService.addFeaturesToRelease(project_replanID, release.getId(), new FeatureListReplan(requirement_replanID));
        List<ResourceListReplan> reslist = new ArrayList<ResourceListReplan>();
        if (!projectSpecific) {
            for (PersonToPReplan pers : PersonToPReplanRepository.findByProjectIdQuery(project_replanID)) {
                reslist.add(new ResourceListReplan(pers.getIdReplan()));
                // Add special availability calc
            }
        } else {
            ProjectToPReplan proj = ProjectToPReplanRepository.getOne(p);
            List<String> part = proj.getParticipants();
            for (String person : part) {
                PersonToPReplan pers = PersonToPReplanRepository.findById(new PersonId(project_replanID, person));
                reslist.add(new ResourceListReplan(pers.getIdReplan()));
            }
        }
        replanService.addResourcesToRelease(project_replanID, releaseId, reslist);
        Plan[] plan = replanService.plan(project_replanID, releaseId);
        Map<String, Set<String>> output = fusePlans(plan);
        List<Responsible> returnobject = createOutput(user, output);
        replanService.deleteRelease(project_replanID, releaseId);
        if (plan != null) {
            List<RecommendReturnSchema> ret = prepareFinal(returnobject, featureSkills, project_replanID);
            return ret.stream().sorted().limit(k).collect(Collectors.toList());
        } else return null;
    }

    public void purge() {
        for (ProjectToPReplan pr : ProjectToPReplanRepository.findAll()) {
            deleteProject(pr.getId());
        }
        PersonToPReplanRepository.deleteAll();
        RequirementToFeatureRepository.deleteAll();
        RejectedPersonRepository.deleteAll();
        ProjectToPReplanRepository.deleteAll();

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


    public Integer addBatch(BatchSchema request, Boolean withAvailability) throws Exception {
        Map<String, Requirement> recs = new HashMap<String, Requirement>();
        for (Requirement r : request.getRequirements()) {
            recs.put(r.getId(), r);
        }
        Map<String, List<String>> personRecs = getPersonRecs(request);
        Map<String, List<String>> recsPerson = getRecsPerson(request);
        Map<String, List<String>> participants = getParticipants(request);
        for (Project p : request.getProjects()) {
            String id = instanciateProject(p, participants);
            List<String> requirementNames = new ArrayList<String>();
            requirementNames.addAll(recs.keySet());
            Map<String, List<SkillListReplan>> allSkills = computeSkillsRequirement(requirementNames, id, recs);
            instanciateFeatureBatch(p.getSpecifiedRequirements(), id, allSkills);
            instanciateResourceBatch(request, recs, personRecs, recsPerson, p, id, withAvailability);
            setHours(request.getParticipants());
        }
        return request.getPersons().size() + request.getProjects().size() + request.getRequirements().size() + request.getResponsibles().size() + request.getParticipants().size();
    }

    private void setHours(List<Participant> participants) {
        for (Participant part: participants) {
            PersonId pers= new PersonId(part.getProject(),part.getPerson());
            PersonToPReplan resource=PersonToPReplanRepository.findById(pers);
            resource.setHours(part.getAvailability());
            PersonToPReplanRepository.save(resource);
        }
    }

    private void instanciateResourceBatch(BatchSchema request, Map<String, Requirement> recs, Map<String, List<String>> personRecs, Map<String, List<String>> recsPerson, Project p, String id, Boolean withAvailability) {
        for (Person person : request.getPersons()) {
            List<SkillListReplan> out;
            if (personRecs.get(person.getUsername()) != null)
                out = computeSkillsPerson(personRecs.get(person.getUsername()), recs, recsPerson);
            else out = new ArrayList<>();
            Double availability = 0.0;
            if (withAvailability) {
                availability = computeAvailability(p.getSpecifiedRequirements(), personRecs, person,recs,p.getId());
            } else availability = 1.0;
            instanciateResource(person, id, out, availability);
        }
    }

    private void instanciateResource(Person person, String id, List<SkillListReplan> skills, Double availability) {
        if (PersonToPReplanRepository.findById(new PersonId(id, person.getUsername())) == null) {
            ResourceReplan resourceReplan = replanService.createResource(person, id, availability);
            PersonToPReplan personTrad = new PersonToPReplan(new PersonId(id, person.getUsername()), id, resourceReplan.getId().toString(), availability);
            PersonToPReplanRepository.save(personTrad);
            replanService.addSkillsToPerson(id, resourceReplan.getId(), skills);
        }
    }

    private Double computeAvailability(List<String> recs, Map<String, List<String>> personRecs, Person person, Map<String, Requirement> requirementMap, String project) {
        List<String> requirements = personRecs.get(person.getUsername());
        List<String> intersection = new ArrayList<String>(requirements);
        List<String> toRemove = new ArrayList<String>(requirements);
        toRemove.removeAll(recs);
        intersection.removeAll(toRemove);
        Double hours = 0.0;
        for (String s : intersection) {
            hours += extractAvailability(requirementMap.get(s).getEffort(),project);
        }
        Double result = calculateAvailability(hours, 40);
        return result;
    }

    private Double calculateAvailability(Double hours, int i) {
        return (max(0, (1 - (hours / 40.0)))) * 100;
    }

    private Integer extractAvailability(Integer s,String project) {
        Effort eff=EffortRepository.getOne(project);
        return eff.getEffort()[s];
    }

    private void instanciateFeatureBatch(List<String> requirement, String id, Map<String, List<SkillListReplan>> skills) {
        for (String rec : requirement) {
            if (RequirementToFeatureRepository.findById(new RequirementId(id, rec)) == null) {
                FeatureReplan featureReplan = replanService.createRequirement(rec, id);
                RequirementToFeature requirementTrad = new RequirementToFeature(new RequirementId(id, rec));
                requirementTrad.setID_Replan(featureReplan.getId().toString());
                requirementTrad.setProjectIdQuery(id);
                RequirementToFeatureRepository.save(requirementTrad);
                replanService.addSkillsToRequirement(id, featureReplan.getId(), skills.get(rec));
            }
        }
    }

    private String instanciateProject(Project p, Map<String, List<String>> participants) {
        String id = null;
        if (ProjectToPReplanRepository.existsById(p.getId())) {
            id = ProjectToPReplanRepository.getOne(p.getId()).getIdReplan().toString();
            replanService.deleteProject(id);
            deleteRelated(id);
            ProjectToPReplanRepository.deleteById(p.getId());
        }
        ProjectReplan projectReplan = replanService.createProject(p);
        id = projectReplan.getId().toString();
        ProjectToPReplan projectTrad = new ProjectToPReplan(p.getId());
        projectTrad.setIdReplan(projectReplan.getId());
        projectTrad.setParticipants(participants.get(p.getId()));
        ProjectToPReplanRepository.save(projectTrad);

        return id;
    }

    private Map<String, List<SkillListReplan>> computeSkillsRequirement(List<String> requirement, String id, Map<String, Requirement> recs) throws Exception {
        TFIDFKeywordExtractor extractor = new TFIDFKeywordExtractor();
        Map<String, List<SkillListReplan>> toret = new HashMap<String, List<SkillListReplan>>();
        List<String> corpus = new ArrayList<String>();
        for (String s : requirement) {
            corpus.add(recs.get(s).getDescription());
        }
        List<Map<String, Double>> keywords = extractor.computeTFIDF(corpus);
        int i = 0;
        System.out.println(keywords.size());
        Map<String, Skill> existingSkills = new HashMap<String, Skill>();
        for (String s : requirement) {
            List<SkillListReplan> recSkills = new ArrayList<SkillListReplan>();
            for (String key : keywords.get(i).keySet()) {
                if (!existingSkills.containsKey(key)) {
                    Skill auxiliar = new Skill(key, 1.0);
                    recs.get(s).addSkill(auxiliar);
                    System.out.println("Keyword :" + key);
                    SkillReplan skill = replanService.createSkill(auxiliar, id);
                    auxiliar.setIdReplan(skill.getId());
                    existingSkills.put(key, auxiliar);
                    recSkills.add(new SkillListReplan(skill.getId(), 1.0));
                } else {
                    recs.get(s).addSkill(existingSkills.get(key));
                    recSkills.add(new SkillListReplan(existingSkills.get(key).getIdReplan(), 1.0));
                }
            }
            toret.put(s, recSkills);
            ++i;
        }
        return toret;
    }

    private List<SkillListReplan> computeSkillsPerson(List<String> oldRecs, Map<String, Requirement> recs, Map<String, List<String>> recsPerson) {
        List<SkillListReplan> toret = new ArrayList<SkillListReplan>();
        Map<Integer, Pair<Double>> appearances = new HashMap<Integer, Pair<Double>>();
        for (String s : oldRecs) {
            for (Skill sk : recs.get(s).getSkills()) {
                if (appearances.containsKey(sk.getIdReplan())) {
                    appearances.put(sk.getIdReplan(), new Pair<Double>(appearances.get(sk.getIdReplan()).p1 + 1.0, appearances.get(sk.getIdReplan()).p2));
                } else {
                    appearances.put(sk.getIdReplan(), new Pair<Double>(1.0, (double) recsPerson.get(s).size()));
                }
            }
        }
        int i = 0;
        for (Integer key : appearances.keySet()) {
            Double ability = calculateWeight(appearances.get(key).p2, appearances.get(key).p1);
            SkillListReplan helper = new SkillListReplan(key, ability);
            toret.add(helper);
            ++i;
        }
        return toret;
    }

    private Double calculateWeight(Double requirement, Double appearances) {
        Double aux = appearances;
        Double aux2 = requirement;
        return aux / aux2;
    }


    private List<Responsible> createOutput(String user, Map<String, Set<String>> output) {
        List<Responsible> returnobject = new ArrayList<Responsible>();
        for (String s : output.keySet()) {
            String username = PersonToPReplanRepository.findByIdReplan(s).getId().getPersonId();
            Set<String> idReplan=output.get(s);
            Set<String> idRecommender=translate(idReplan);
            Set<String> inRetty = reject(user, idRecommender, username);
            for (String req : inRetty) {
                returnobject.add(new Responsible(username, req));
            }
        }
        return returnobject;
    }

    private Map<String, Set<String>> fusePlans(Plan[] plan) {
        Map<String, Set<String>> output = new HashMap<String, Set<String>>();
        for (int i = 0; i < plan.length && i < 10; i++) {
            Plan auxplan = plan[i];
            Map<String, Set<String>> aux = parsePlan(auxplan);
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


    private Map<String, Set<String>> parsePlan(Plan plan) {
        List<ResourceReplan> resources = plan.getResources();
        Map<String, Set<String>> toret = new HashMap<>();
        for (ResourceReplan res : resources) {
            toret.put(res.getId().toString(), res.getFeaturesWorkedOn());
        }
        return toret;
    }

    private List<RecommendReturnSchema> prepareFinal(List<Responsible> returnobject, FeatureSkill featureSkill, String project_replanID) {
        List<RecommendReturnSchema> ret = new ArrayList<RecommendReturnSchema>();
        for (Responsible res : returnobject) {
            PersonToPReplan traductor = PersonToPReplanRepository.findById(new PersonId(project_replanID, res.getPerson()));
            List<ResourceSkill> resSkill = replanService.getResourceSkill(project_replanID, traductor.getIdReplan());
            Double total = 0.0;
            for (ResourceSkill resskill : resSkill) {
                for (String done : featureSkill.getSkillIds()) {
                    if (resskill.getSkill_id().equals(done)) {
                        total = total + resskill.getWeight();
                    }
                }
            }
            Double amount = (double) featureSkill.getSkillIds().size();
            Double appropiateness = total / amount;
            Double availability = traductor.getAvailability();
            ret.add(new RecommendReturnSchema(res.getRequirement(), res.getPerson(), appropiateness, availability / 100.0));
        }
        return ret;
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

    public void deleteProject(String id) {
        deleteRelated(id);
        String idReplan = ProjectToPReplanRepository.getOne(id).getIdReplan().toString();
        replanService.deleteProject(idReplan);
        ProjectToPReplanRepository.deleteById(id);
    }

    public void extract(ExtractTest request) throws Exception {
        TFIDFKeywordExtractor extractor = new TFIDFKeywordExtractor();
        List<Map<String, Double>> res = extractor.computeTFIDF(request.getCorpus());
        Integer i = 0;
        for (Map<String, Double> map : res) {
            System.out.println("------------------------------");
            System.out.println("Document Number " + i);
            System.out.println("------------------------------");
            for (String s : map.keySet()) {
                if (map.get(s)>2)
                System.out.println(s + "  " + map.get(s));
            }
            ++i;
        }
    }


/*
    public void extract2(ExtractTest request) throws IOException {
        RAKEKeywordExtractor extractor = new RAKEKeywordExtractor();
        List<Map<String, Double>> res = extractor.extractKeywords(request.getCorpus());
        Integer i = 0;
        for (Map<String, Double> map : res) {
            System.out.println("------------------------------");
            System.out.println("Document Number " + i);
            System.out.println("------------------------------");
            for (String s : map.keySet()) {
                if (map.get(s) > 10)
                    System.out.println(s + "  " + map.get(s));
            }
            ++i;
        }
    }
*/
    private Map<String, List<String>> getRecsPerson(BatchSchema request) {
        Map<String, List<String>> recsPerson = new HashMap<String, List<String>>();
        for (Responsible resp : request.getResponsibles()) {
            if (recsPerson.containsKey(resp.getRequirement())) {
                recsPerson.get(resp.getRequirement()).add(resp.getPerson());
            } else {
                List<String> aux = new ArrayList<String>();
                aux.add(resp.getPerson());
                recsPerson.put(resp.getRequirement(), aux);
            }
        }
        return recsPerson;
    }

    private Map<String, List<String>> getPersonRecs(BatchSchema request) {
        Map<String, List<String>> personRecs = new HashMap<String, List<String>>();
        for (Responsible resp : request.getResponsibles()) {
            if (personRecs.containsKey(resp.getPerson())) {
                personRecs.get(resp.getPerson()).add(resp.getRequirement());
            } else {
                List<String> aux = new ArrayList<String>();
                aux.add(resp.getRequirement());
                personRecs.put(resp.getPerson(), aux);
            }
        }
        return personRecs;
    }

    private Map<String, List<String>> getParticipants(BatchSchema request) {
        Map<String, List<String>> participants = new HashMap<String, List<String>>();
        if (request.getParticipants() != null) {
            for (Participant par : request.getParticipants()) {
                if (participants.containsKey(par.getProject())) {
                    participants.get(par.getProject()).add(par.getPerson());
                } else {
                    List<String> aux = new ArrayList<String>();
                    aux.add(par.getPerson());
                    participants.put(par.getProject(), aux);
                }
            }
        }
        return participants;
    }

    private class Pair<T> {
        T p1, p2;

        Pair(T p1, T p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

    }

}
