package upc.stakeholdersrecommender.service;

import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import upc.stakeholdersrecommender.domain.*;
import upc.stakeholdersrecommender.domain.Schemas.*;
import upc.stakeholdersrecommender.domain.keywords.TFIDFKeywordExtractor;
import upc.stakeholdersrecommender.entity.*;
import upc.stakeholdersrecommender.repository.*;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.Double.max;
import static java.lang.Math.min;

@Service
@Transactional
public class StakeholdersRecommenderService {

    @Value("${skill.dropoff.days}")
    private String dropoffDays;


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
    @Autowired
    private KeywordExtractionModelRepository KeywordExtractionModelRepository;


    public List<RecommendReturnSchema> recommend(RecommendSchema request, int k, Boolean projectSpecific, String organization) throws Exception {
        String p = request.getProject().getId();
        List<RecommendReturnSchema> ret;
        List<PersonSR> persList = new ArrayList<>();
        RequirementSR req;
        RequirementSR newReq=new RequirementSR();
        Requirement requeriment=request.getRequirement();
        requeriment.setDescription(requeriment.getDescription()+" "+requeriment.getName());
        newReq.setProjectIdQuery(request.getProject().getId());
        newReq.setID(new RequirementSRId(request.getProject().getId(),request.getRequirement().getId(),organization));
        newReq.setSkills(new TFIDFKeywordExtractor().computeTFIDFSingular(requeriment,KeywordExtractionModelRepository.getOne(organization).getModel()));
        List<String> comps=new ArrayList<>();
        if (request.getRequirement().getRequirementParts()!=null) {
            for (RequirementPart l : request.getRequirement().getRequirementParts()) {
                comps.add(l.getId());
            }
        }
        newReq.setComponent(comps);
        RequirementSRRepository.save(newReq);
        req=newReq;
            if (!projectSpecific) {
                for (PersonSR pers : PersonSRRepository.findByOrganization(organization)) {
                    if (hasTime(pers,organization)) {
                        PersonSR per=new PersonSR();
                        per.setComponents(pers.getComponents());
                        per.setAvailability(1.0);
                        per.setHours(pers.getHours());
                        per.setSkills(pers.getSkills());
                        per.setName(pers.getName());
                        persList.add(per);
                    }
                }
            } else {
                persList.addAll(PersonSRRepository.findByProjectIdQueryAndOrganization(p,organization));
            }
            List<PersonSR> clean=removeRejected(persList, request.getUser().getUsername(),organization);
            Double hours=100.0;
            if (projectSpecific) {
                Double effort = request.getRequirement().getEffort();
                if (EffortRepository.findById(new ProjectSRId(request.getProject().getId(),organization))!=null) {
                    HashMap<Double,Double> effMap=EffortRepository.findById(new ProjectSRId(request.getProject().getId(),organization)).getEffortMap();
                    if (effMap.containsKey(effort)) {
                        hours = effMap.get(effort);
                    }
                    else {
                        hours=effort;
                        Effort eff=EffortRepository.getOne(request.getProject().getId());
                        effMap.put(effort,effort);
                        eff.setEffortMap(effMap);
                        EffortRepository.save(eff);
                    }
                }
            }
            PersonSR[] bestPeople = computeBestStakeholders(clean, req,hours, k,projectSpecific);
        ret = prepareFinal(bestPeople, req);
        return ret;
    }

    private List<PersonSR> removeRejected(List<PersonSR> persList, String user, String organization) {
        List<PersonSR> newList = new ArrayList<>();
        RejectedPerson rej=RejectedPersonRepository.findByUser(new RejectedPersonId(user,organization));
        if (rej != null) {
            for (PersonSR pers : persList) {
                if (!rej.getDeleted().containsKey(pers.getName())) {
                    newList.add(pers);
                }
            }
        }
        else newList=persList;
        return newList;
    }

    private List<RecommendReturnSchema> prepareFinal(PersonSR[] people, RequirementSR req) {
        List<RecommendReturnSchema> ret = new ArrayList<>();
        for (PersonSR pers : people) {
            Map<String, Skill> skillTrad = new HashMap<>();
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
            Double appropiateness;
            if (amount==0.0) {
                appropiateness=0.0;
            }
            else appropiateness = total / amount;
            Double availability = pers.getAvailability();
            PersonMinimal min=new PersonMinimal();
            min.setUsername(pers.getName());
            ret.add(new RecommendReturnSchema(new RequirementMinimal(req.getID().getRequirementId()),min, appropiateness, availability));
        }
        Collections.sort(ret,
                Comparator.comparingDouble(RecommendReturnSchema::getApropiatenessScore).reversed());
        return ret;
    }

    private PersonSR[] computeBestStakeholders(List<PersonSR> persList, RequirementSR req, Double hours,int k, Boolean projectSpecific) {
        List<Pair<PersonSR, Double>> valuesForSR = new ArrayList<>();

        for (PersonSR person : persList) {
            Double sum = 0.0;
            Double compSum = 0.0;
            Double resComp = 0.0;
            for (String s : req.getSkills()) {
                for (Skill j : person.getSkills()) {
                    if (s.equals(j.getName())) {
                        sum += j.getWeight();
                    }
                }
            }
            if (req.getComponent()!=null) {
                for (String s : req.getComponent()) {
                    for (Component j : person.getComponents()) {
                        if (s.equals(j.getName())) {
                            compSum += j.getWeight();
                        }
                    }
                }
                resComp = compSum / req.getComponent().size();
            }
            Double res;
            if (req.getSkills().size()==0) {
                res=0.0;
            }
            else {
                res = sum / req.getSkills().size();
            }
            res = res * 3 + person.getAvailability() + resComp * 10;
            if (projectSpecific && person.getAvailability()>=hours/person.getHours()) {
                Pair<PersonSR, Double> valuePair = new Pair<>(person, res);
                valuesForSR.add(valuePair);
            }
            else if (!projectSpecific) {
                Pair<PersonSR, Double> valuePair = new Pair<>(person, res);
                valuesForSR.add(valuePair);
            }
        }
        Collections.sort(valuesForSR,
                Comparator.comparingDouble(Pair<PersonSR, Double>::getSecond).reversed());
        if (k>=valuesForSR.size()) {
            k=valuesForSR.size();
        }
        PersonSR[] out=new PersonSR[k];
        for (int i = 0; i < k && i<valuesForSR.size(); ++i) {
            out[i] = valuesForSR.get(i).getFirst();
        }
        return out;
    }

    private boolean hasTime(PersonSR pers,String organization) {
        boolean res = false;
        List<PersonSR> work = PersonSRRepository.findByNameAndOrganization(pers.getId().getPersonId(),organization);
        for (PersonSR per : work) {
            if (per.getAvailability() > 0) {
                res = true;
                break;
            }
        }
        return res;
    }

    private void purge(String organization) {
        if (ProjectRepository.findByOrganization(organization)!=null)ProjectRepository.deleteByOrganization(organization);
        if (PersonSRRepository.findByOrganization(organization)!=null)PersonSRRepository.deleteByOrganization(organization);
        if (RequirementSRRepository.findByOrganization(organization)!=null)RequirementSRRepository.deleteByOrganization(organization);
        //if (RejectedPersonRepository.findByOrganization(organization)!=null)RejectedPersonRepository.deleteByOrganization(organization);
        if (KeywordExtractionModelRepository.existsById(organization)) KeywordExtractionModelRepository.deleteById(organization);

    }

    public void recommend_reject(String rejectedId, String userId, String requirementId, String organization) {
        if (RejectedPersonRepository.findByUser(new RejectedPersonId(userId,organization))!=null) {
            RejectedPerson rejected = RejectedPersonRepository.findByUser(new RejectedPersonId(userId,organization));
            if (rejected.getDeleted().containsKey(rejectedId)) {
                HashSet<String> aux=rejected.getDeleted().get(rejectedId);
                aux.add(requirementId);
                Map<String,HashSet<String>> auxMap=rejected.getDeleted();
                auxMap.put(rejectedId,aux);
                rejected.setDeleted(auxMap);
            } else {
                HashSet<String> aux=new HashSet<>();
                aux.add(requirementId);
                Map<String,HashSet<String>> auxMap=rejected.getDeleted();
                auxMap.put(rejectedId,aux);
                rejected.setDeleted(auxMap);
            }
            RejectedPersonRepository.saveAndFlush(rejected);
        } else {
            RejectedPerson reject = new RejectedPerson(new RejectedPersonId(userId,organization));
            HashMap<String, HashSet<String>> aux = new HashMap<>();
            HashSet<String> auxset = new HashSet<>();
            auxset.add(requirementId);
            aux.put(rejectedId, auxset);
            reject.setDeleted(aux);
            RejectedPersonRepository.saveAndFlush(reject);
        }
    }


    public Integer addBatch(BatchSchema request, Boolean withAvailability, Boolean withComponent, String organization,Boolean autoMapping) throws Exception {
        purge(organization);
        Map<String, Requirement> recs = new HashMap<>();
        for (Requirement r : request.getRequirements()) {
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
            Date dtIn = inFormat.parse(r.getModified_at());
            r.setModified(dtIn);
            r.setDescription(r.getDescription()+" "+r.getName());
            recs.put(r.getId(), r);
        }
        Map<String, List<String>> personRecs = getPersonRecs(request);
        Map<String, List<Participant>> participants = getParticipants(request);
        Map<String, Map<String, Double>> allSkills = computeAllSkillsRequirement(recs,organization);
        Map<String, Integer> skillfrequency = getSkillFrequency(allSkills);
        Map<String, Map<String, Double>> allComponents = new HashMap<>();
        Map<String, Integer> componentFrequency = new HashMap<>();
        if (withComponent) {
            for (Requirement req : request.getRequirements()) {
                Map<String, Double> component=new HashMap<>();
                for (RequirementPart str:req.getRequirementParts()) {
                    component.put(str.getId(),0.0);
                    if (componentFrequency.containsKey(str.getId())) {
                        componentFrequency.put(str.getId(),componentFrequency.get(str.getId()+1));
                    }
                    else componentFrequency.put(str.getId(),+1);
                }
                allComponents.put(req.getId(),component);
            }
            extractDate(recs, allComponents);
        }
        for (Project proj : request.getProjects()) {
            if (autoMapping) {
                Effort effortMap = new Effort();
                HashMap<Double, Double> eff = new HashMap<>();
                    for (String r : proj.getSpecifiedRequirements()) {
                        Requirement aux = recs.get(r);
                        if (!eff.containsKey(aux.getEffort())) {
                            eff.put(aux.getEffort(), aux.getEffort());
                        }
                    }
                effortMap.setEffortMap(eff);
                effortMap.setId(new ProjectSRId(proj.getId(),organization));
                if (EffortRepository.findById(new ProjectSRId(proj.getId(),organization))!=null) EffortRepository.deleteById(new ProjectSRId(proj.getId(),organization));
                EffortRepository.save(effortMap);
            }
            String id = instanciateProject(proj, participants.get(proj.getId()),organization);
            Map<String, Double> hourMap = new HashMap<>();
            for (Participant par : participants.get(proj.getId())) {
                hourMap.put(par.getPerson(), par.getAvailability());
            }
            instanciateFeatureBatch(proj.getSpecifiedRequirements(), id, allSkills, recs, withComponent, allComponents, organization);
            instanciateResourceBatch(hourMap, request.getPersons(), recs, allSkills, personRecs, skillfrequency, proj.getSpecifiedRequirements(), id, withAvailability, withComponent, allComponents,componentFrequency,organization);
        }
        return request.getPersons().size() + request.getProjects().size() + request.getRequirements().size() + request.getResponsibles().size() + request.getParticipants().size();
    }

    private void extractDate(Map<String, Requirement> recs, Map<String, Map<String, Double>> allComponents) {
        Date dat=new Date();
        computeTimeFactor(recs, allComponents, dat);
    }

    private void computeTimeFactor(Map<String, Requirement> recs, Map<String, Map<String, Double>> allComponents, Date dat) {
        for (String s : allComponents.keySet()) {
            Requirement req = recs.get(s);
            long diffInMillies = Math.abs(dat.getTime() - req.getModified().getTime());
            long diffDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            Map<String, Double> aux = allComponents.get(s);
            for (String j : aux.keySet()) {
                aux.put(j, 1.0 - min(0.5, diffDays * (0.5 / Double.parseDouble(dropoffDays))));
            }
            allComponents.put(s, aux);
        }
    }

    private Map<String, Integer> getSkillFrequency(Map<String, Map<String, Double>> allSkills) {
        Map<String, Integer> skillfrequency = new HashMap<>();
        for (String s : allSkills.keySet()) {
            for (String j : allSkills.get(s).keySet()) {
                if (!skillfrequency.containsKey(j)) {
                    skillfrequency.put(j, 1);
                } else {
                    skillfrequency.put(j, skillfrequency.get(j) + 1);
                }
            }
        }
        return skillfrequency;
    }


    private void instanciateResourceBatch(Map<String, Double> part, List<Person> persons, Map<String, Requirement> recs, Map<String, Map<String, Double>> allSkills, Map<String, List<String>> personRecs, Map<String, Integer> skillFrequency, List<String> specifiedReq, String id, Boolean withAvailability, Boolean withComponent
    , Map<String,Map<String,Double>> allComponents, Map<String,Integer> componentFrequency, String organization) throws Exception {
        List<PersonSR> toSave = new ArrayList<>();
        for (Person person : persons) {
            List<Skill> skills;
            List<Component> components;
            if (personRecs.get(person.getUsername()) != null) {
                skills = computeSkillsPerson(personRecs.get(person.getUsername()), allSkills, skillFrequency);
                if (withComponent) components = computeComponentsPerson(personRecs.get(person.getUsername()),allComponents, componentFrequency);
                else components=new ArrayList<>();
            } else{
                skills = new ArrayList<>();
                components = new ArrayList<>();
            }
            Double availability;
            if (withAvailability) {
                availability = computeAvailability(specifiedReq, personRecs, person, recs, id,part.get(person.getUsername()),organization);
            } else availability = 1.0;
            PersonSR per = new PersonSR(new PersonSRId(id, person.getUsername(),organization), id, availability, skills,organization);
            per.setHours(part.get(per.getName()));
            per.setComponents(components);
            per.setAvailability(availability);
            toSave.add(per);
        }
        PersonSRRepository.saveAll(toSave);
    }

    private List<Component> computeComponentsPerson(List<String> oldRecs,Map<String, Map<String,Double>> allComponents, Map<String,Integer> componentFrequency) {
        List<Component> toret = new ArrayList<>();
        Map<String, SinglePair<Double>> appearances = getAppearances(oldRecs, allComponents, componentFrequency);
        for (String key : appearances.keySet()) {
            Double ability = calculateWeight(appearances.get(key).p2, appearances.get(key).p1);
            Component helper = new Component(key, ability);
            toret.add(helper);
        }
        return toret;

    }

    private Map<String, SinglePair<Double>> getAppearances(List<String> oldRecs, Map<String, Map<String, Double>> allComponents, Map<String, Integer> componentFrequency) {
        Map<String, SinglePair<Double>> appearances = new HashMap<>();
        for (String s : oldRecs) {
            Map<String, Double> help = allComponents.get(s);
            for (String sk : help.keySet()) {
                if (appearances.containsKey(sk)) {
                    appearances.put(sk, new SinglePair<>(appearances.get(sk).p1 + help.get(sk), appearances.get(sk).p2));
                } else {
                    appearances.put(sk, new SinglePair<>(help.get(sk), (double) componentFrequency.get(sk)));
                }
            }
        }
        return appearances;
    }

    private Double computeAvailability(List<String> recs, Map<String, List<String>> personRecs, Person person, Map<String, Requirement> requirementMap, String project, Double totalHours, String organization) throws Exception {
        List<String> requirements = personRecs.get(person.getUsername());
        List<String> intersection = new ArrayList<>(requirements);
        List<String> toRemove = new ArrayList<>(requirements);
        toRemove.removeAll(recs);
        intersection.removeAll(toRemove);
        Double hours = 0.0;
        for (String s : intersection) {
            hours += extractAvailability(requirementMap.get(s).getEffort(), project,organization);
        }
        return calculateAvailability(hours,totalHours);
    }

    private Double calculateAvailability(Double hours, Double i) {
        return max(0, (1 - (hours / i)));
    }

    private Double extractAvailability(Double s, String project,String organization) throws Exception {
        if (EffortRepository.findById(new ProjectSRId(project,organization))==null) {
            throw new Exception();
        }
        Effort eff = EffortRepository.findById(new ProjectSRId(project,organization));
        return eff.getEffortMap().get(s);
    }

    private void instanciateFeatureBatch(List<String> requirement, String id, Map<String, Map<String, Double>> keywordsForReq, Map<String, Requirement> recs, Boolean withComponent,Map<String,Map<String,Double>> allComponents, String organization) {
        List<RequirementSR> reqs = new ArrayList<>();
        for (String rec : requirement) {
            RequirementSR req = new RequirementSR(recs.get(rec), id,organization);
            ArrayList<String> aux = new ArrayList<>(keywordsForReq.get(rec).keySet());
            req.setSkills(aux);
            if (withComponent) req.setComponent(new ArrayList<>(allComponents.get(rec).keySet()));
            reqs.add(req);
        }
        RequirementSRRepository.saveAll(reqs);
    }

    private String instanciateProject(Project proj, List<Participant> participants, String organization) {
        String id = proj.getId();
        ProjectSR projectSRTrad = new ProjectSR(new ProjectSRId(proj.getId(),organization));
        List<String> parts = new ArrayList<>();
        for (Participant par : participants) {
            parts.add(par.getPerson());
        }
        projectSRTrad.setParticipants(parts);
        ProjectRepository.save(projectSRTrad);
        return id;
    }


    private Map<String, Map<String, Double>> computeAllSkillsRequirement(Map<String, Requirement> recs,String organization) throws IOException {
        TFIDFKeywordExtractor extractor = new TFIDFKeywordExtractor();
        //Extract map with (Requirement / KeywordValue)
        Map<String, Map<String, Double>> keywords = extractor.computeTFIDF(recs.values());
        Date dat = new Date();

        //Transform the map from (Requirement / KeywordValue) to (Requirement / SkillFactor)

        //Skill factor is a linear function, dropping off lineally up to 0.5, based on the days
        //since the requirement was last touched
        HashMap<String,Integer> mod=extractor.getCorpusFrequency();
        KeywordExtractionModel toSave=new KeywordExtractionModel();
        toSave.setModel(mod);
        toSave.setId(organization);
        KeywordExtractionModelRepository.save(toSave);
        KeywordExtractionModelRepository.flush();
        computeTimeFactor(recs, keywords, dat);
        return keywords;
    }


    private List<Skill> computeSkillsPerson(List<String> oldRecs, Map<String, Map<String, Double>> recs, Map<String, Integer> skillsFrequency) {
        List<Skill> toret = new ArrayList<>();
        Map<String, SinglePair<Double>> appearances = getAppearances(oldRecs, recs, skillsFrequency);
        for (String key : appearances.keySet()) {
            Double ability = calculateWeight(appearances.get(key).p2, appearances.get(key).p1);
            Skill helper = new Skill(key, ability);
            toret.add(helper);
        }
        return toret;
    }

    private Double calculateWeight(Double requirement, Double appearances) {
        return appearances / requirement;
    }

    private Map<String, List<String>> getPersonRecs(BatchSchema request) {
        Map<String, List<String>> personRecs = new HashMap<>();
        for (Responsible resp : request.getResponsibles()) {
            if (personRecs.containsKey(resp.getPerson())) {
                List<String> aux = personRecs.get(resp.getPerson());
                aux.add(resp.getRequirement());
                personRecs.put(resp.getPerson(), aux);
            } else {
                List<String> aux = new ArrayList<>();
                aux.add(resp.getRequirement());
                personRecs.put(resp.getPerson(), aux);
            }
        }
        return personRecs;
    }

    private Map<String, List<Participant>> getParticipants(BatchSchema request) {
        Map<String, List<Participant>> participants = new HashMap<>();
        if (request.getParticipants() != null) {
            for (Participant par : request.getParticipants()) {
                if (participants.containsKey(par.getProject())) {
                    participants.get(par.getProject()).add(par);
                } else {
                    List<Participant> aux = new ArrayList<>();
                    aux.add(par);
                    participants.put(par.getProject(), aux);
                }
            }
        }
        return participants;
    }

    public List<ProjectKeywordSchema> extractKeywords(String organization,BatchSchema batch) {
        List<ProjectKeywordSchema> res=new ArrayList<>();
        for (Project j:batch.getProjects()) {
            ProjectKeywordSchema proje=new ProjectKeywordSchema();
            List<KeywordReturnSchema> reqs=new ArrayList<>();
            String id=j.getId();
            proje.setProjectId(id);
            for (RequirementSR req : RequirementSRRepository.findByOrganizationAndProj(organization,id)) {
                KeywordReturnSchema key = new KeywordReturnSchema();
                key.setRequirement(req.getID().getRequirementId());
                key.setSkills(req.getSkills());
                reqs.add(key);
            }
            proje.setRequirements(reqs);
            res.add(proje);
        }
        return res;
    }

    private class SinglePair<T> {
        T p1, p2;

        SinglePair(T p1, T p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

    }


}
