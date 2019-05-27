package upc.stakeholdersrecommender.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import upc.stakeholdersrecommender.domain.*;
import upc.stakeholdersrecommender.domain.Schemas.*;
import upc.stakeholdersrecommender.domain.keywords.TFIDFKeywordExtractor;
import upc.stakeholdersrecommender.entity.*;
import upc.stakeholdersrecommender.repository.*;
import upc.stakeholdersrecommender.repository.RequirementSRRepository;
import org.apache.commons.math3.util.Pair;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Double.max;

@Service
public class StakeholdersRecommenderService {

    @Autowired
    private PersonSRRepository PersonSRRepository;
    @Autowired
    private ProjectRepository ProjectRepository;
    @Autowired
    private RequirementSRRepository RequirementSRRepository;
    @Autowired
    private RejectedPersonRepository RejectedPersonRepository;
    @Autowired
    private EffortRepository EffortRepository;


    public List<RecommendReturnSchema> recommend(RecommendSchema request, int k, Boolean projectSpecific) throws Exception {
        String p = request.getProject();
        String r = request.getRequirement();
        List<RecommendReturnSchema> ret = new ArrayList<RecommendReturnSchema>();
        List<PersonSR> persList = new ArrayList<PersonSR>();
        if (RequirementSRRepository.findById(new RequirementSRId(p, r)) != null) {
            RequirementSR req = RequirementSRRepository.findById(new RequirementSRId(p, r));
            if (!projectSpecific) {
                for (PersonSR pers : PersonSRRepository.findByProjectIdQuery(p)) {
                    if (hasTime(pers)) {
                        pers.setAvailability(1.0);
                        persList.add(pers);
                    }
                }
            } else {
                persList.addAll(PersonSRRepository.findByProjectIdQuery(p));
            }
            removeRejected(persList,request.getUser());
            PersonSR[] bestPeople=computeBestStakeholders(persList,req, k);
            ret=prepareFinal(bestPeople,req);
        }
        else throw new Exception();
        return ret;
    }

    private void removeRejected(List<PersonSR> persList, String user) {
        List<PersonSR> newList=new ArrayList<PersonSR>();
        if (RejectedPersonRepository.existsById(user)) {
            RejectedPerson rej = RejectedPersonRepository.getOne(user);
            if (rej != null) {
                for (PersonSR pers : persList) {
                    if (!rej.getDeleted().containsKey(pers.getName())) {
                        newList.add(pers);
                    }
                }
            }
        }
    }

    private List<RecommendReturnSchema> prepareFinal(PersonSR[] people,RequirementSR req) {
        List<RecommendReturnSchema> ret = new ArrayList<RecommendReturnSchema>();
        for (PersonSR pers : people) {
            Map<String, Skill> skillTrad = new HashMap<String, Skill>();
            List<String> reqSkills = req.getSkills();
            for (Skill sk : pers.getSkills()) {
                skillTrad.put(sk.getName(), sk);
            }
            Double total = 0.0;
            for (String skill : skillTrad.keySet()) {
                for (String done : reqSkills) {
                    if (skill.equals(done)) {
                        total = total + skillTrad.get(skill).getWeight();
                    }
                }
            }
            Double amount = (double) req.getSkills().size();
            Double appropiateness = total / amount;
            Double availability = pers.getAvailability();
            ret.add(new RecommendReturnSchema(req.getID().getRequirementId(), pers.getName(), appropiateness, availability ));
        }
        Collections.sort(ret,
                Comparator.comparingDouble(RecommendReturnSchema::getApropiatenessScore).reversed());

        return ret;
    }

    private PersonSR[] computeBestStakeholders(List<PersonSR> persList, RequirementSR req, int k) {
        List<Pair<PersonSR, Double>> valuesForSR = new ArrayList<Pair<PersonSR, Double>>();

        for (PersonSR person : persList) {
            Double sum = 0.0;
            for (String s : req.getSkills()) {
                for (Skill j : person.getSkills()) {
                    if (s.equals(j.getName())) {
                        sum += j.getWeight();
                    }
                }
            }
            Double res = sum / req.getSkills().size();
            res = res + person.getAvailability();
            Pair<PersonSR, Double> valuePair = new Pair<PersonSR, Double>(person, res);
            valuesForSR.add(valuePair);
        }
        Collections.sort(valuesForSR,
                Comparator.comparingDouble(Pair<PersonSR,Double>::getSecond).reversed());
        PersonSR[] out= new PersonSR[k];
        for (int i=0;i<k;++i) {
            System.out.println(valuesForSR.get(i).getSecond());
            out[i]=valuesForSR.get(i).getFirst();
        }
        return out;
    }

    private boolean hasTime(PersonSR pers) {
        Boolean res = false;
        List<PersonSR> work = PersonSRRepository.findByName(pers.getId().getPersonId());
        for (PersonSR per : work) {
            if (per.getAvailability() > 0) {
                res = true;
                break;
            }
        }
        return res;
    }

    public void purge() {
        for (ProjectSR pr : ProjectRepository.findAll()) {
            deleteProject(pr.getId());
        }
        PersonSRRepository.deleteAll();
        RequirementSRRepository.deleteAll();
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
        Map<String, List<Participant>> participants = getParticipants(request);
        for (Project p : request.getProjects()) {
            String id = instanciateProject(p, participants.get(p.getId()));
            Map<String, Integer> hourMap = new HashMap<String, Integer>();
            for (Participant par : participants.get(p.getId())) {
                hourMap.put(par.getPerson(), par.getAvailability());
            }
            Map<String, Map<String, Double>> allSkills = computeAllSkillsRequirement(id, recs);
            Map<String,Integer> skillfrequency=new HashMap<String,Integer>();
            for (String s:allSkills.keySet()) {
                for (String j:allSkills.get(s).keySet()) {
                    if (!skillfrequency.containsKey(j)) {
                        skillfrequency.put(j,1);
                    }
                    else {
                        skillfrequency.put(j,skillfrequency.get(j)+1);
                    }
                }

            }

            instanciateFeatureBatch(p.getSpecifiedRequirements(), id, allSkills,recs);
            instanciateResourceBatch(hourMap,request.getPersons(), recs,allSkills, personRecs, skillfrequency, p.getSpecifiedRequirements(), id, withAvailability);
        }
        return request.getPersons().size() + request.getProjects().size() + request.getRequirements().size() + request.getResponsibles().size() + request.getParticipants().size();
    }

    private void setHours(List<Participant> participants) {
        for (Participant part : participants) {
            String projId = ProjectRepository.getOne(part.getProject()).getId();
            PersonSRId pers = new PersonSRId(projId, part.getPerson());
            PersonSR resource = PersonSRRepository.findById(pers);
            resource.setHours(part.getAvailability());
            PersonSRRepository.save(resource);
        }
    }

    private void instanciateResourceBatch(Map<String,Integer> part, List<Person> persons, Map<String,Requirement> recs , Map<String,Map<String,Double>> allSkills, Map<String, List<String>> personRecs, Map<String, Integer> skillFrequency, List<String> specifiedReq, String id, Boolean withAvailability) throws Exception {
        List<PersonSR> toSave=new ArrayList<PersonSR>();
        for (Person person : persons) {
            List<Skill> skills;
            if (personRecs.get(person.getUsername()) != null) {
                skills = computeSkillsPerson(personRecs.get(person.getUsername()), allSkills, skillFrequency);
            }
            else skills = new ArrayList<Skill>();
            Double availability;
            if (withAvailability) {
                availability = computeAvailability(specifiedReq, personRecs, person,recs,id);
            } else availability = 1.0;
            PersonSR per=new PersonSR(new PersonSRId(id,person.getUsername()), id, availability,skills);
            per.setHours(part.get(per.getName()));
            toSave.add(per);
        }
        PersonSRRepository.saveAll(toSave);
    }

    private Double computeAvailability(List<String> recs, Map<String, List<String>> personRecs, Person person, Map<String, Requirement> requirementMap, String project) throws Exception {
        List<String> requirements = personRecs.get(person.getUsername());
        List<String> intersection = new ArrayList<String>(requirements);
        List<String> toRemove = new ArrayList<String>(requirements);
        toRemove.removeAll(recs);
        intersection.removeAll(toRemove);
        Double hours = 0.0;
        for (String s : intersection) {
            hours += extractAvailability(requirementMap.get(s).getEffort(), project);
        }
        Double result = calculateAvailability(hours, PersonSRRepository.findById(new PersonSRId(project, person.getUsername())).getHours());
        return result;
    }

    private Double calculateAvailability(Double hours, Integer i) {
        return (max(0, (1 - (hours / i.doubleValue()))));
    }

    private Double extractAvailability(Integer s, String project) throws Exception {
        if (!EffortRepository.existsById(project)) {
            throw new Exception();
        }
        Effort eff = EffortRepository.getOne(project);
        return eff.getEffort()[s];
    }

    private void instanciateFeatureBatch(List<String> requirement, String id, Map<String, Map<String, Double>> keywordsForReq, Map<String, Requirement> recs) {
        List<RequirementSR> reqs = new ArrayList<RequirementSR>();
        for (String rec : requirement) {
            if (RequirementSRRepository.findById(new RequirementSRId(id, rec)) == null) {
                RequirementSR req = new RequirementSR(recs.get(rec), id);
                ArrayList<String> aux = new ArrayList<String>();
                for (String s : keywordsForReq.get(rec).keySet()) {
                    aux.add(s);
                }
                req.setSkills(aux);
                reqs.add(req);
            }
        }
        RequirementSRRepository.saveAll(reqs);
    }

    private String instanciateProject(Project p, List<Participant> participants) {
        String id = null;
        if (ProjectRepository.existsById(p.getId())) {
            id = ProjectRepository.getOne(p.getId()).getId();
            deleteRelated(id);
            ProjectRepository.deleteById(p.getId());
        }
        id = p.getId();
        ProjectSR projectSRTrad = new ProjectSR(p.getId());
        List<String> parts = new ArrayList<String>();
        for (Participant par : participants) {
            parts.add(par.getPerson());
        }
        projectSRTrad.setParticipants(parts);
        ProjectRepository.save(projectSRTrad);
        return id;
    }


    private Map<String, Map<String, Double>> computeAllSkillsRequirement(String id, Map<String, Requirement> recs) throws IOException {
        TFIDFKeywordExtractor extractor = new TFIDFKeywordExtractor();
        Map<String, List<String>> toret = new HashMap<String, List<String>>();
        List<String> corpus = new ArrayList<String>();
        for (Requirement r : recs.values()) {
            corpus.add(r.getDescription());
        }
        Map<String, Map<String, Double>> keywords = extractor.computeTFIDF(recs.values());
        return keywords;
    }



    private List<Skill> computeSkillsPerson(List<String> oldRecs, Map<String,Map<String, Double>> recs, Map<String, Integer> skillsFrequency) {
        List<Skill> toret = new ArrayList<Skill>();
        Map<String, SinglePair<Double>> appearances = new HashMap<String, SinglePair<Double>>();
        for (String s : oldRecs) {
            for (String sk : recs.get(s).keySet()) {
                if (appearances.containsKey(sk)) {
                    appearances.put(sk, new SinglePair<Double>(appearances.get(sk).p1 + 1.0, appearances.get(sk).p2));
                } else {
                    appearances.put(sk, new SinglePair<Double>(1.0, (double) skillsFrequency.get(sk)));
                }
            }
        }
        for (String key : appearances.keySet()) {
            Double ability = calculateWeight(appearances.get(key).p2, appearances.get(key).p1);
            Skill helper = new Skill(key, ability);
            toret.add(helper);
        }
        return toret;
    }

    private Double calculateWeight(Double requirement, Double appearances) {
        Double aux = appearances;
        Double aux2 = requirement;
        return aux / aux2;
    }


    private void deleteRelated(String id) {
        PersonSRRepository.deleteByProjectIdQuery(id);
        RequirementSRRepository.deleteByProjectIdQuery(id);
    }

    public void deleteProject(String id) {
        deleteRelated(id);
        ProjectRepository.deleteById(id);
    }

    public void extract2(List<Requirement> request) throws Exception {
        // PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
        TFIDFKeywordExtractor extractor = new TFIDFKeywordExtractor();
        Map<String, Map<String, Double>> res = extractor.computeTFIDF(request);
        Map<String, Requirement> traductor = new HashMap<String, Requirement>();
        for (Requirement r : request) {
            traductor.put(r.getId(), r);
        }
        OutputKeywordExtraction output = new OutputKeywordExtraction();
        PrintStream out = new PrintStream(new FileOutputStream("out.txt", true));
        System.setOut(out);
        Random r = new Random();
        List<String> id = new ArrayList<String>();
        List<String> description = new ArrayList<String>();
        List<List<String>> keywords = new ArrayList<List<String>>();
        int high = 100;
        int random;
        for (String requir : res.keySet()) {
            random = r.nextInt(high);
            if (random == 50) {
                Map<String, Double> map = res.get(requir);
                id.add(traductor.get(requir).getId());
                description.add(traductor.get(requir).getDescription());
                keywords.add(new ArrayList<String>(map.keySet()));
            }
        }
        for (String i : id) {
            System.out.println(i);
        }
        for (String d : description) {
            System.out.println(d);
        }
        for (List<String> k : keywords) {
            System.out.println();
            for (String k2 : k) {
                System.out.print(k2 + " ");
            }
        }
    }

    public OutputKeywordExtraction extract(List<RequirementDocument> request) throws Exception {
        TFIDFKeywordExtractor extractor = new TFIDFKeywordExtractor();
        Map<String, Map<String, Double>> res = extractor.computeTFIDF(request);
        Map<String, RequirementDocument> traductor = new HashMap<String, RequirementDocument>();
        for (RequirementDocument r : request) {
            traductor.put(r.getId(), r);
        }
        OutputKeywordExtraction output = new OutputKeywordExtraction();
        for (String requir : res.keySet()) {
            Map<String, Double> map = res.get(requir);
            ExtractedRequirement extracted = new ExtractedRequirement();
            extracted.setDescription(traductor.get(requir).getDescription());
            extracted.setId(traductor.get(requir).getId());
            List<String> keys = new ArrayList<String>(map.keySet());
            extracted.setKeywords(keys);
            output.addExtractedRequirement(extracted);
        }
        return output;
    }

    private Map<String, List<String>> getPersonRecs(BatchSchema request) {
        Map<String, List<String>> personRecs = new HashMap<String, List<String>>();
        for (Responsible resp : request.getResponsibles()) {
            if (personRecs.containsKey(resp.getPerson())) {
                List<String> aux = personRecs.get(resp.getPerson());
                aux.add(resp.getRequirement());
                personRecs.put(resp.getPerson(), aux);
            } else {
                List<String> aux = new ArrayList<String>();
                aux.add(resp.getRequirement());
                personRecs.put(resp.getPerson(), aux);
            }
        }
        return personRecs;
    }

    private Map<String, List<Participant>> getParticipants(BatchSchema request) {
        Map<String, List<Participant>> participants = new HashMap<String, List<Participant>>();
        if (request.getParticipants() != null) {
            for (Participant par : request.getParticipants()) {
                if (participants.containsKey(par.getProject())) {
                    participants.get(par.getProject()).add(par);
                } else {
                    List<Participant> aux = new ArrayList<Participant>();
                    aux.add(par);
                    participants.put(par.getProject(), aux);
                }
            }
        }
        return participants;
    }

    private class SinglePair<T> {
        T p1, p2;

        SinglePair(T p1, T p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

    }



}
