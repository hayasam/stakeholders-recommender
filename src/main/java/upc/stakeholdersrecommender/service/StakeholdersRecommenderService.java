package upc.stakeholdersrecommender.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import upc.stakeholdersrecommender.domain.*;
import upc.stakeholdersrecommender.domain.Schemas.*;
import upc.stakeholdersrecommender.domain.keywords.TFIDFKeywordExtractor;
import upc.stakeholdersrecommender.domain.replan.*;
import upc.stakeholdersrecommender.entity.*;
import upc.stakeholdersrecommender.entity.ProjectSR;
import upc.stakeholdersrecommender.repository.*;
import upc.stakeholdersrecommender.repository.RequirementReplanRepository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Double.max;

@Service
public class StakeholdersRecommenderService {

    @Autowired
    private PersonReplanRepository PersonReplanRepository;
    @Autowired
    private ProjectRepository ProjectRepository;
    @Autowired
    private RequirementReplanRepository RequirementReplanRepository;
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
        String project_replanID = ProjectRepository.getOne(p).getIdReplan().toString();
        String requirement_replanID = "";
        if (RequirementReplanRepository.findById(new RequirementReplanId(project_replanID, r)) != null) {
            requirement_replanID = RequirementReplanRepository.findById(new RequirementReplanId(project_replanID, r)).getID_Replan();
        } else throw new Exception();
        ReleaseReplan release = replanService.createRelease(project_replanID);
        Integer releaseId = release.getId();
        String user = request.getUser();

        FeatureSkill featureSkills = replanService.getFeatureSkill(project_replanID, requirement_replanID);
        replanService.addFeaturesToRelease(project_replanID, release.getId(), new FeatureListReplan(requirement_replanID));
        List<ResourceListReplan> reslist = new ArrayList<ResourceListReplan>();
        if (!projectSpecific) {
            for (PersonReplan pers : PersonReplanRepository.findByProjectIdQuery(project_replanID)) {
                if (hasTime(pers)) reslist.add(new ResourceListReplan(pers.getIdReplan()));
            }
            //Set availability to 1 to all people in reslist!!!
        } else {
            ProjectSR proj = ProjectRepository.getOne(p);
            List<String> part = proj.getParticipants();
            for (String person : part) {
                PersonReplan pers = PersonReplanRepository.findById(new PersonReplanId(project_replanID, person));
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

    private boolean hasTime(PersonReplan pers) {
        Boolean res=false;
        List<PersonReplan> work= PersonReplanRepository.findByName(pers.getId().getPersonId());
        for (PersonReplan per:work) {
            if (per.getAvailability()>0){
                res=true;
                break;
            }
        }
        return res;
    }

    public void purge() {
        for (ProjectSR pr : ProjectRepository.findAll()) {
            deleteProject(pr.getId());
        }
        PersonReplanRepository.deleteAll();
        RequirementReplanRepository.deleteAll();
        RejectedPersonRepository.deleteAll();
        ProjectRepository.deleteAll();

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
            Map<String, List<SkillListReplan>> allSkills = computeAllSkillsRequirement(id, recs);
            instanciateFeatureBatch(p.getSpecifiedRequirements(), id, allSkills,recs);
            instanciateResourceBatch(request, recs, personRecs, recsPerson, p, id, withAvailability);
            setHours(request.getParticipants());
        }
        return request.getPersons().size() + request.getProjects().size() + request.getRequirements().size() + request.getResponsibles().size() + request.getParticipants().size();
    }

    private void setHours(List<Participant> participants) {
        for (Participant part: participants) {
            String id_replan=ProjectRepository.getOne(part.getProject()).getIdReplan().toString();
            PersonReplanId pers= new PersonReplanId(id_replan,part.getPerson());
            PersonReplan resource= PersonReplanRepository.findById(pers);
            resource.setHours(part.getAvailability());
            PersonReplanRepository.save(resource);
        }
    }

    private void instanciateResourceBatch(BatchSchema request, Map<String, Requirement> recs, Map<String, List<String>> personRecs, Map<String, List<String>> recsPerson,Project p, String id, Boolean withAvailability) throws Exception {
        List<Person> personList=new ArrayList<Person>();
        Map<String,Person> personIdentifier=new HashMap<String,Person>();
        for (Person person : request.getPersons()) {
            List<SkillListReplan> skills;
            if (personRecs.get(person.getUsername()) != null) {
                skills = computeSkillsPerson(personRecs.get(person.getUsername()), recs, recsPerson);
            }
            else skills = new ArrayList<>();
            Double availability;
            if (withAvailability) {
                availability = computeAvailability(p.getSpecifiedRequirements(), personRecs, person,recs,p.getId());
            } else availability = 1.0;
            person.setAvailability(availability);
            person.setSkills(skills);
            personList.add(person);
            personIdentifier.put(person.getUsername(),person);
        }
        ResourceReplan[] resources=replanService.createResources(personList,id);

        for (ResourceReplan res:resources){
            Person pers=personIdentifier.get(res.getName());
            PersonReplan personTrad = new PersonReplan(new PersonReplanId(id, res.getName()), id, res.getId().toString(), pers.getAvailability());
            PersonReplanRepository.save(personTrad);
            replanService.addSkillsToPerson(id, res.getId(), pers.getSkills());

        }

    }
/*
    private void instanciateResource(Person person, String id, List<SkillListReplan> skills, Double availability) {
        if (PersonReplanRepository.findById(new PersonReplanId(id, person.getUsername())) == null) {
            ResourceReplan resourceReplan = replanService.createResource(person, id, availability);
            PersonReplan personTrad = new PersonReplan(new PersonReplanId(id, person.getUsername()), id, resourceReplan.getId().toString(), availability);
            PersonReplanRepository.save(personTrad);
            replanService.addSkillsToPerson(id, resourceReplan.getId(), skills);
        }
    }
*/
    private Double computeAvailability(List<String> recs, Map<String, List<String>> personRecs, Person person, Map<String, Requirement> requirementMap, String project) throws Exception {
        List<String> requirements = personRecs.get(person.getUsername());
        List<String> intersection = new ArrayList<String>(requirements);
        List<String> toRemove = new ArrayList<String>(requirements);
        toRemove.removeAll(recs);
        intersection.removeAll(toRemove);
        Double hours = 0.0;
        for (String s : intersection) {
            hours += extractAvailability(requirementMap.get(s).getEffort(),project);
        }
        Double result = calculateAvailability(hours, PersonReplanRepository.findById(new PersonReplanId(project,person.getUsername())).getHours());
        return result;
    }

    private Double calculateAvailability(Double hours, Integer i) {
        return (max(0, (1 - (hours / i.doubleValue())))) * 100;
    }

    private Double extractAvailability(Integer s,String project) throws Exception {
        if (!EffortRepository.existsById(project)) {
            throw new Exception();
        }
        Effort eff = EffortRepository.getOne(project);
        return eff.getEffort()[s];
    }
/*
    private void instanciateFeatureBatch(List<String> requirement, String id, Map<String, List<SkillListReplan>> skills, Map<String, Requirement> recs) {
        for (String rec : requirement) {
            if (RequirementReplanRepository.findById(new RequirementReplanId(id, rec)) == null) {
                FeatureReplan featureReplan = replanService.createRequirement(rec, id);
                RequirementReplanRepository requirementTrad = new RequirementReplanRepository(new RequirementReplanId(id, rec));
                requirementTrad.setID_Replan(featureReplan.getId().toString());
                requirementTrad.setProjectIdQuery(id);
                RequirementReplanRepository.save(requirementTrad);
                replanService.addSkillsToRequirement(id, featureReplan.getId(), skills.get(rec));
            }
        }
    }
*/

    private void instanciateFeatureBatch(List<String> requirement, String id, Map<String, List<SkillListReplan>> skills, Map<String, Requirement> recs) {
        Map<Integer,Requirement> codeToReq=new HashMap<Integer,Requirement>();
        List<FeatureReplan> featureList=new ArrayList<FeatureReplan>();
        int code=0;
        for (String rec : requirement) {
            if (RequirementReplanRepository.findById(new RequirementReplanId(id, rec)) == null) {
                FeatureReplan fet=new FeatureReplan(recs.get(rec));
                codeToReq.put(code,recs.get(rec));
                fet.setCode(code);
                code++;
                featureList.add(fet);
            }
        }
        FeatureReplan[] fetsReplan=replanService.createRequirements(featureList,id);
        for (FeatureReplan f:fetsReplan) {

            Requirement req=codeToReq.get(f.getCode());
            String rec=req.getId();
            RequirementReplan requirementTrad = new RequirementReplan(new RequirementReplanId(id, rec));
            requirementTrad.setID_Replan(f.getId().toString());
            requirementTrad.setProjectIdQuery(id);
            RequirementReplanRepository.save(requirementTrad);
            replanService.addSkillsToRequirement(id, f.getId(), skills.get(rec));

        }
    }

    private String instanciateProject(Project p, Map<String, List<String>> participants) {
        String id = null;
        if (ProjectRepository.existsById(p.getId())) {
            id = ProjectRepository.getOne(p.getId()).getIdReplan().toString();
            replanService.deleteProject(id);
            deleteRelated(id);
            ProjectRepository.deleteById(p.getId());
        }
        ProjectReplan projectReplan = replanService.createProject(p);
        id = projectReplan.getId().toString();
        ProjectSR projectSRTrad = new ProjectSR(p.getId());
        projectSRTrad.setIdReplan(projectReplan.getId());
        projectSRTrad.setParticipants(participants.get(p.getId()));
        ProjectRepository.save(projectSRTrad);

        return id;
    }
/*
    private Map<String, List<SkillListReplan>> computeSkillsRequirements(List<String> requirement, String id, Map<String, Requirement> recs) throws Exception {
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
*/
    private Map<String, List<SkillListReplan>> computeAllSkillsRequirement(String id, Map<String, Requirement> recs) throws IOException {
        TFIDFKeywordExtractor extractor = new TFIDFKeywordExtractor();
        Map<String, List<SkillListReplan>> toret = new HashMap<String, List<SkillListReplan>>();
        List<String> corpus = new ArrayList<String>();
        for (Requirement r : recs.values()) {
            corpus.add(r.getDescription());
        }
        Map<String,Map<String, Double>> keywords = extractor.computeTFIDF(recs.values());
        int i = 0;
        Map<String, List<String>> skillsForRequirement= new HashMap<String,List<String>>();
        Map<String, Skill> existingSkills = new HashMap<String, Skill>();
        for (String s : recs.keySet()) {
            List<String> recSkills = new ArrayList<String>();
            for (String key : keywords.get(s).keySet()) {
                if (!existingSkills.containsKey(key)) {
                    existingSkills.put(key,new Skill(key,1.0));
                    recSkills.add(key);
                } else {
                    recs.get(s).addSkill(existingSkills.get(key));
                    recSkills.add(key);
                }
            }
            skillsForRequirement.put(s, recSkills);
            ++i;
        }
        SkillReplan[] skillsReplan=replanService.createSkills(existingSkills.values(), id);
        Map<String,SkillReplan> nameToSkillReplan=new HashMap<String,SkillReplan>();
        for (SkillReplan skR:skillsReplan) {
            nameToSkillReplan.put(skR.getName(),skR);
        }
        for (String req:skillsForRequirement.keySet()) {
            List<SkillListReplan> aux=new ArrayList<SkillListReplan>();
            for (String skillRequirement:skillsForRequirement.get(req)) {
                SkillReplan auxiliar2= nameToSkillReplan.get(skillRequirement);
                SkillListReplan listReplan=new SkillListReplan(auxiliar2);
                Skill normalSkill=new Skill(auxiliar2.getName(),1.0,auxiliar2.getId());
                recs.get(req).addSkill(normalSkill);
                aux.add(listReplan);
            }
            toret.put(req,aux);
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
        for (Integer key : appearances.keySet()) {
            Double ability = calculateWeight(appearances.get(key).p2, appearances.get(key).p1);
            SkillListReplan helper = new SkillListReplan(key, ability);
            toret.add(helper);
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
            String username = PersonReplanRepository.findByIdReplan(s).getId().getPersonId();
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
            PersonReplan traductor = PersonReplanRepository.findById(new PersonReplanId(project_replanID, res.getPerson()));
            List<ResourceSkill> resSkills = replanService.getResourceSkill(project_replanID, traductor.getIdReplan());
            Double total = 0.0;
            for (ResourceSkill resSkill : resSkills) {
                for (String done : featureSkill.getSkillIds()) {
                    if (resSkill.getSkill_id().equals(done)) {
                        total = total + resSkill.getWeight();
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
            aux.add(RequirementReplanRepository.findByIdReplan(s).getID().getRequirementId());
        }
        return aux;
    }

    private void deleteRelated(String id) {
        RequirementSkillsRepository.deleteByProjectIdQuery(id);
        PersonReplanRepository.deleteByProjectIdQuery(id);
        RequirementReplanRepository.deleteByProjectIdQuery(id);
    }

    public void deleteProject(String id) {
        deleteRelated(id);
        String idReplan = ProjectRepository.getOne(id).getIdReplan().toString();
        replanService.deleteProject(idReplan);
        ProjectRepository.deleteById(id);
    }

    public void extract(List<Requirement> request) throws Exception {
       // PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
        TFIDFKeywordExtractor extractor = new TFIDFKeywordExtractor();
        Map<String,Map<String, Double>> res = extractor.computeTFIDF(request);
        Map<String,Requirement> traductor=new HashMap<String,Requirement>();
        for (Requirement r:request) {
            traductor.put(r.getId(),r);
        }
        PrintStream out = new PrintStream(new FileOutputStream("out.txt", true));
        System.setOut(out);
        Random r = new Random();
        int high = 100;
        int random;
        for (String requir : res.keySet()) {
            random = r.nextInt(high);
            if (random == 50) {
                System.out.println("");
                Map<String, Double> map = res.get(requir);
                System.out.println(traductor.get(requir).getId());
                System.out.println(traductor.get(requir).getDescription());
                System.out.println("Keywords :");
                for (String s : map.keySet()) {
                    System.out.print(s+" ");
                }
            }
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
