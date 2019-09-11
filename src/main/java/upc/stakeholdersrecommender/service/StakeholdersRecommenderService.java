package upc.stakeholdersrecommender.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneandone.compositejks.SslContextUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import upc.stakeholdersrecommender.domain.*;
import upc.stakeholdersrecommender.domain.Preprocess.PreprocessService;
import upc.stakeholdersrecommender.domain.Schemas.*;
import upc.stakeholdersrecommender.domain.keywords.RAKEKeywordExtractor;
import upc.stakeholdersrecommender.domain.keywords.TFIDFKeywordExtractor;
import upc.stakeholdersrecommender.domain.rilogging.Log;
import upc.stakeholdersrecommender.domain.rilogging.LogArray;
import upc.stakeholdersrecommender.entity.*;
import upc.stakeholdersrecommender.repository.*;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
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
    @Value("${person.hours.default}")
    private Double hoursDefault;


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
    @Autowired
    private WordEmbedding WordEmbedding;
    @Autowired
    private PreprocessService Preprocess;


    public List<RecommendReturnSchema> recommend(RecommendSchema request, int k, Boolean projectSpecific, String organization, Integer test) throws Exception {
        String p = request.getProject().getId();
        List<RecommendReturnSchema> ret;
        List<PersonSR> persList = new ArrayList<>();
        RequirementSR req;
        RequirementSR newReq = new RequirementSR();
        Requirement requirement = request.getRequirement();
        requirement.setDescription(requirement.getDescription() + ". " + requirement.getName());
        newReq.setProjectIdQuery(request.getProject().getId());
        newReq.setId(new RequirementSRId(request.getProject().getId(), request.getRequirement().getId(), organization));
        ProjectSR pro = ProjectRepository.findById(new ProjectSRId(request.getProject().getId(), organization));
        Boolean rake = pro.getRake();
        Boolean bugzilla = pro.getBugzilla();
        if (bugzilla) {
            newReq.setSkills(Preprocess.preprocessSingular(requirement, test));
        } else {
            if (!rake) {
                Integer size = pro.getRecSize();
                newReq.setSkills(new TFIDFKeywordExtractor().computeTFIDFSingular(requirement, KeywordExtractionModelRepository.getOne(organization).getModel(), size));
            } else newReq.setSkills(new RAKEKeywordExtractor().computeTFIDFSingular(requirement));
        }
        List<String> comps = new ArrayList<>();
        if (request.getRequirement().getRequirementParts() != null) {
            for (RequirementPart l : request.getRequirement().getRequirementParts()) {
                comps.add(l.getId());
            }
        }
        newReq.setComponent(comps);
        RequirementSRRepository.save(newReq);
        req = newReq;
        if (!projectSpecific) {
            Set<String> uniquePersons = new HashSet<>();
            for (PersonSR pers : PersonSRRepository.findByOrganization(organization)) {
                if (!uniquePersons.contains(pers.getName())) {
                    if (hasTime(pers, organization)) {
                        uniquePersons.add(pers.getName());
                        PersonSR per = new PersonSR();
                        per.setComponents(pers.getComponents());
                        per.setAvailability(1.0);
                        per.setHours(pers.getHours());
                        per.setSkills(pers.getSkills());
                        per.setName(pers.getName());
                        persList.add(per);
                    }
                }
            }
        } else {
            persList.addAll(PersonSRRepository.findByProjectIdQueryAndOrganization(p, organization));
        }
        List<PersonSR> clean = removeRejected(persList, request.getUser().getUsername(), organization, request.getRequirement().getId());
        Double hours = 100.0;
        if (projectSpecific) {
            Double effort = request.getRequirement().getEffort();
            Effort e = EffortRepository.findById(new ProjectSRId(request.getProject().getId(), organization));
            if (e != null) {
                Map<Double, Double> effMap = e.getEffortMap();
                if (effMap.containsKey(effort)) {
                    hours = effMap.get(effort);
                } else {
                    hours = effort;
                    Effort eff = e;
                    effMap.put(effort, effort);
                    eff.setEffortMap(effMap);
                    EffortRepository.save(eff);
                }
            }
        }
        PersonSR[] bestPeople = computeBestStakeholders(clean, req, hours, k, projectSpecific);
        ret = prepareFinal(bestPeople, req);
        return ret;
    }

    private List<PersonSR> removeRejected(List<PersonSR> persList, String user, String organization, String requirement) {
        List<PersonSR> newList = new ArrayList<>();
        RejectedPerson rej = RejectedPersonRepository.findByUser(new RejectedPersonId(user, organization));
        if (rej != null) {
            for (PersonSR pers : persList) {
                if (!rej.getDeleted().containsKey(pers.getName())) {
                    newList.add(pers);
                } else if (!rej.getDeleted().get(pers.getName()).contains(requirement)) {
                    newList.add(pers);
                }
            }
        } else newList = persList;
        return newList;
    }

    private List<RecommendReturnSchema> prepareFinal(PersonSR[] people, RequirementSR req) throws IOException {
        List<RecommendReturnSchema> ret = new ArrayList<>();
        for (PersonSR pers : people) {
            Map<String, Skill> skillTrad = new HashMap<>();
            Double appropiateness = getAppropiateness(req, pers, skillTrad);
            Double availability = pers.getAvailability();
            PersonMinimal min = new PersonMinimal();
            min.setUsername(pers.getName());
            ret.add(new RecommendReturnSchema(new RequirementMinimal(req.getId().getRequirementId()), min, appropiateness, availability));
        }
        Collections.sort(ret,
                Comparator.comparingDouble(RecommendReturnSchema::getAppropiatenessScore).reversed());
        return ret;
    }

    private PersonSR[] computeBestStakeholders(List<PersonSR> persList, RequirementSR req, Double hours, int k, Boolean projectSpecific) throws IOException {
        List<Pair<PersonSR, Double>> valuesForSR = new ArrayList<>();

        for (PersonSR person : persList) {
            Double sum = 0.0;
            Double compSum = 0.0;
            Double resComp = 0.0;
            for (String s : req.getSkillsSet()) {
                Double weightToAdd = 0.0;
                Skill mostSimilarSkill = null;
                for (Skill j : person.getSkills()) {
                    if (j.getName().equals(s)) {
                        weightToAdd = 100.0;
                        sum = sum + j.getWeight();
                        break;
                    } else {
                        Double val = WordEmbedding.computeSimilarity(j.getName(), s);
                        if (val > weightToAdd) {
                            weightToAdd = val;
                            mostSimilarSkill = j;
                        }
                    }
                }
                if (weightToAdd != 100.0) {
                    if (weightToAdd!=0.0&&mostSimilarSkill!=null)
                        sum = sum + weightToAdd * mostSimilarSkill.getWeight();
                }
            }
            if (req.getComponent() != null) {
                for (String s : req.getComponent()) {
                    for (Skill j : person.getComponents()) {
                        if (s.equals(j.getName())) {
                            compSum += j.getWeight();
                        }
                    }
                }
                resComp = compSum / req.getComponent().size();
            }
            Double res;
            if (req.getSkillsSet().size() == 0) {
                res = 0.0;
            } else {
                res = sum / req.getSkillsSet().size();
            }
            Map<String, Skill> skillTrad = new HashMap<>();
            Double appropiateness = getAppropiateness(req, person, skillTrad);
            res = res * 3 + person.getAvailability() + resComp * 10;
            if (projectSpecific && person.getAvailability() >= hours / person.getHours() && appropiateness != 0) {
                Pair<PersonSR, Double> valuePair = new Pair<>(person, res);
                valuesForSR.add(valuePair);
            } else if (!projectSpecific && appropiateness != 0) {
                Pair<PersonSR, Double> valuePair = new Pair<>(person, res);
                valuesForSR.add(valuePair);
            }
        }
        Collections.sort(valuesForSR,
                Comparator.comparingDouble(Pair<PersonSR, Double>::getSecond).reversed());
        if (k >= valuesForSR.size()) {
            k = valuesForSR.size();
        }
        PersonSR[] out = new PersonSR[k];
        for (int i = 0; i < k && i < valuesForSR.size(); ++i) {
            out[i] = valuesForSR.get(i).getFirst();
        }
        return out;
    }

    private Double getAppropiateness(RequirementSR req, PersonSR person, Map<String, Skill> skillTrad) throws IOException {
        Set<String> reqSkills = req.getSkillsSet();
        Double total = 0.0;
        if (person.getSkills()!=null&&person.getSkills().size()>0) {
            for (Skill sk : person.getSkills()) {
                skillTrad.put(sk.getName(), sk);
            }
            for (String done : reqSkills) {
                Double weightToAdd = 0.0;
                String mostSimilarWord = "";
                for (String skill : skillTrad.keySet()) {
                        if (skill.equals(done)) {
                            weightToAdd = 100.0;
                            total = total + skillTrad.get(skill).getWeight();
                            break;
                        } else {
                            Double val = WordEmbedding.computeSimilarity(skill, done);
                            if (val > weightToAdd) {
                                weightToAdd = val;
                                mostSimilarWord = skill;
                            }
                        }
                    }
                    if (weightToAdd != 100.0) {
                        if (weightToAdd!=0.0)
                        total = total + (weightToAdd * skillTrad.get(mostSimilarWord).getWeight());
                    }
                }
            }
        Double amount = (double) req.getSkillsSet().size();
        Double appropiateness;
        if (amount == 0.0) {
            appropiateness = 0.0;
        } else appropiateness = total / amount;
        return appropiateness;
    }

    private boolean hasTime(PersonSR pers, String organization) {
        boolean res = false;
        List<PersonSR> work = PersonSRRepository.findByNameAndOrganization(pers.getId().getPersonId(), organization);
        for (PersonSR per : work) {
            if (per.getAvailability() > 0) {
                res = true;
                break;
            }
        }
        return res;
    }

    private void purge(String organization) {
        if (ProjectRepository.findByOrganization(organization) != null)
            ProjectRepository.deleteByOrganization(organization);
        if (PersonSRRepository.findByOrganization(organization) != null)
            PersonSRRepository.deleteByOrganization(organization);
        if (RequirementSRRepository.findByOrganization(organization) != null)
            RequirementSRRepository.deleteByOrganization(organization);
        if (KeywordExtractionModelRepository.existsById(organization))
            KeywordExtractionModelRepository.deleteById(organization);

    }

    public void recommend_reject(String rejectedId, String userId, String requirementId, String organization) {
        if (RejectedPersonRepository.findByUser(new RejectedPersonId(userId, organization)) != null) {
            RejectedPerson rejected = RejectedPersonRepository.findByUser(new RejectedPersonId(userId, organization));
            if (rejected.getDeleted().containsKey(rejectedId)) {
                HashSet<String> aux = rejected.getDeleted().get(rejectedId);
                aux.add(requirementId);
                Map<String, HashSet<String>> auxMap = rejected.getDeleted();
                auxMap.put(rejectedId, aux);
                rejected.setDeleted(auxMap);
            } else {
                HashSet<String> aux = new HashSet<>();
                aux.add(requirementId);
                Map<String, HashSet<String>> auxMap = rejected.getDeleted();
                auxMap.put(rejectedId, aux);
                rejected.setDeleted(auxMap);
            }
            RejectedPersonRepository.saveAndFlush(rejected);
        } else {
            RejectedPerson reject = new RejectedPerson(new RejectedPersonId(userId, organization));
            HashMap<String, HashSet<String>> aux = new HashMap<>();
            HashSet<String> auxset = new HashSet<>();
            auxset.add(requirementId);
            aux.put(rejectedId, auxset);
            reject.setDeleted(aux);
            RejectedPersonRepository.saveAndFlush(reject);
        }
    }


    public Integer addBatch(BatchSchema request, Boolean withAvailability, Boolean withComponent, String organization, Boolean autoMapping, Boolean bugzillaPreprocessing, Boolean logging, Integer test) throws Exception {
        purge(organization);
        verify(request);
        Map<String, Requirement> recs = new HashMap<>();
        List<Requirement> requeriments;
        if (bugzillaPreprocessing) {
            requeriments = Preprocess.preprocess(request.getRequirements(), test);
        } else {
            requeriments = request.getRequirements();
        }
        for (Requirement r : requeriments) {
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
            Date dtIn = inFormat.parse(r.getModified_at());
            r.setModified(dtIn);
            r.setDescription(r.getDescription() + ". " + r.getName());
            recs.put(r.getId(), r);
        }
        Map<String, Set<String>> personRecs = getPersonRecs(request);
        Set<String> persons = getPersons(request);
        Map<String, List<Participant>> participants = getParticipants(request);
        Boolean rake = true;
        Map<String, Map<String, Double>> allSkills;
        if (!bugzillaPreprocessing) {
            if (requeriments.size() > 100) rake = false;
            if (rake) allSkills = computeAllSkillsRequirementRAKE(recs, organization);
            else allSkills = computeAllSkillsRequirement(recs, organization);
        } else {
            allSkills = computeAllSkillsNoMethod(recs);
        }
        Map<String, Integer> skillfrequency = getSkillFrequency(allSkills);
        Map<String, Map<String, Double>> allComponents = new HashMap<>();
        Map<String, Integer> componentFrequency = new HashMap<>();
        if (withComponent) {
            for (Requirement req : request.getRequirements()) {
                Map<String, Double> component = new HashMap<>();
                if (req.getRequirementParts() != null)
                    for (RequirementPart str : req.getRequirementParts()) {
                        component.put(str.getId(), 0.0);
                        if (componentFrequency.containsKey(str.getId())) {
                            componentFrequency.put(str.getId(), componentFrequency.get(str.getId()) + 1);
                        } else componentFrequency.put(str.getId(), 1);
                    }
                allComponents.put(req.getId(), component);
            }
            allComponents = extractDate(recs, allComponents);
        }
        Set<String> projs = new HashSet<>();
        Set<String> seenPersons = new HashSet<>();
        Integer recSize = request.getRequirements().size();

        Pair<Map<String, Map<String, Double>>, Map<String, Map<String, Pair<Integer, Integer>>>> pair = null;
        Map<String, Integer> loggingFrequency = null;

        if (logging) {
            pair = getUserLogging(bugzillaPreprocessing, rake, organization, recSize, test);
            loggingFrequency = getSkillFrequency(pair.getFirst());
        }


        for (Project proj : request.getProjects()) {
            projs.add(proj.getId());
            if (autoMapping) {
                Effort effortMap = new Effort();
                HashMap<Double, Double> eff = new HashMap<>();
                if (proj.getSpecifiedRequirements() != null) {
                    for (String r : proj.getSpecifiedRequirements()) {
                        Requirement aux = recs.get(r);
                        if (!eff.containsKey(aux.getEffort())) {
                            eff.put(aux.getEffort(), aux.getEffort());
                        }
                    }
                }
                effortMap.setEffortMap(eff);
                effortMap.setId(new ProjectSRId(proj.getId(), organization));
                if (EffortRepository.findById(new ProjectSRId(proj.getId(), organization)) != null)
                    EffortRepository.deleteById(new ProjectSRId(proj.getId(), organization));
                EffortRepository.save(effortMap);
            }
            List<Participant> part = new ArrayList<>();
            if (participants.containsKey(proj.getId())) part = participants.get(proj.getId());
            String id = instanciateProject(proj, part, organization, rake, recSize, bugzillaPreprocessing);
            Map<String, Double> hourMap = new HashMap<>();
            for (Participant par : part) {
                hourMap.put(par.getPerson(), par.getAvailability());
            }
            for (Participant p : part) {
                seenPersons.add(p.getPerson());
            }
            instanciateFeatureBatch(proj.getSpecifiedRequirements(), id, allSkills, recs, withComponent, allComponents, organization);
            instanciateResourceBatch(hourMap, part, recs, allSkills, personRecs, skillfrequency, proj.getSpecifiedRequirements(), id, withAvailability, withComponent, allComponents, componentFrequency, organization, pair, loggingFrequency);
        }
        persons.removeAll(seenPersons);
        instanciateLeftovers(persons, projs, allSkills, personRecs, skillfrequency, withComponent, allComponents, componentFrequency, organization, pair, loggingFrequency);
        Integer particips = 0;
        if (request.getParticipants() != null) particips = request.getParticipants().size();
        return request.getPersons().size() + request.getProjects().size() + request.getRequirements().size() + request.getResponsibles().size() + particips;
    }

    private Map<String, Map<String, Double>> computeAllSkillsNoMethod(Map<String, Requirement> recs) {
        Map<String, Map<String, Double>> ret = new HashMap<>();
        for (String s : recs.keySet()) {
            Requirement r = recs.get(s);
            Set<String> helper = new HashSet<>();
            for (String h : r.getDescription().split(" ")) {
                h=h.replace(".","");
                if (!h.equals("")&&h.length()>1)
                    helper.add(h);
            }
            Map<String, Double> aux = new HashMap<>();
            for (String j : helper) {
                aux.put(j, 0.0);
            }
            ret.put(s, aux);
        }
        return  computeTimeFactor(recs,ret,new Date());
    }


    private void verify(BatchSchema request) throws Exception {
        Set<String> rec = new HashSet<>();
        for (Requirement r : request.getRequirements()) {
            if (r.getId().length() > 255) throw new Exception("Requirement id exceeds character size of 255");
            if (rec.contains(r.getId())) throw new Exception("Requirement id " + r.getId() + " is repeated");
            rec.add(r.getId());
        }
        Set<String> proj = new HashSet<>();
        for (Project p : request.getProjects()) {
            if (p.getId().length() > 255) throw new Exception("Project id exceeds character size of 255");
            if (proj.contains(p.getId())) throw new Exception("Project id " + p.getId() + " is repeated");
            for (String a : p.getSpecifiedRequirements())
                if (!rec.contains(a)) throw new Exception("Specified requirement " + a + " doesn't exist");
            proj.add(p.getId());
        }
        Set<String> person = new HashSet<>();
        for (PersonMinimal p : request.getPersons()) {
            if (p.getUsername().length() > 255) throw new Exception("Requirement id exceeds character size of 255");
            if (person.contains(p.getUsername())) throw new Exception("Person id " + p.getUsername() + " is repeated");
            person.add(p.getUsername());
        }
        for (Responsible r : request.getResponsibles()) {
            if (!rec.contains(r.getRequirement()))
                throw new Exception("Person assigned to non-existent requirement " + r.getRequirement());
            if (!person.contains(r.getPerson()))
                throw new Exception("Requirement assigned to non-existent person " + r.getPerson());
        }
        if (request.getParticipants() != null) {
            for (Participant p : request.getParticipants()) {
                if (!person.contains(p.getPerson()))
                    throw new Exception("Project assigned to non-existent person " + p.getPerson());
                if (!proj.contains(p.getProject()))
                    throw new Exception("Person assigned to non-existent project " + p.getProject());
                if (p.getAvailability() != null && p.getAvailability() < 0)
                    throw new Exception("Availability must be in a range from 1.0 to 0.0");
            }
        }
    }

    private void instanciateLeftovers(Set<String> persons, Set<String> oldIds, Map<String, Map<String, Double>> allSkills, Map<String, Set<String>> personRecs, Map<String, Integer> skillFrequency, Boolean withComponent
            , Map<String, Map<String, Double>> allComponents, Map<String, Integer> componentFrequency, String organization, Pair<Map<String, Map<String, Double>>, Map<String, Map<String, Pair<Integer, Integer>>>> pair, Map<String, Integer> loggingFrequency) throws JsonProcessingException {
        String newId = RandomStringUtils.random(15, true, true);
        while (oldIds.contains(newId)) newId = RandomStringUtils.random(15, true, true);
        List<PersonSR> toSave = new ArrayList<>();
        for (String s : persons) {
            List<Skill> skills;
            List<Skill> components;
            if (personRecs.get(s) != null) {
                skills = computeSkillsPerson(personRecs.get(s), allSkills, skillFrequency, pair, s, loggingFrequency);
                if (withComponent)
                    components = computeComponentsPerson(personRecs.get(s), allComponents, componentFrequency, s);
                else components = new ArrayList<>();
            } else {
                skills = new ArrayList<>();
                components = new ArrayList<>();
            }
            PersonSR per = new PersonSR();
            per.setName(s);
            per.setSkills(skills);
            per.setHours(hoursDefault);
            per.setProjectIdQuery(newId);
            per.setOrganization(organization);
            per.setComponents(components);
            per.setAvailability(1.0);
            per.setId(new PersonSRId(newId, s, organization));
            toSave.add(per);
        }
        PersonSRRepository.saveAll(toSave);
    }

    private Set<String> getPersons(BatchSchema request) {
        Set<String> s = new HashSet<>();
        for (PersonMinimal p : request.getPersons()) {
            s.add(p.getUsername());
        }
        return s;
    }

    private Map<String, Map<String, Double>> extractDate(Map<String, Requirement> recs, Map<String, Map<String, Double>> allComponents) {
        Date dat = new Date();
        return computeTimeFactor(recs, allComponents, dat);
    }

    private Map<String, Map<String, Double>> computeTimeFactor(Map<String, Requirement> recs, Map<String, Map<String, Double>> allComponents, Date dat) {
        Map<String, Map<String, Double>> scaledKeywords = new HashMap<>();
        for (String s : allComponents.keySet()) {
            Requirement req = recs.get(s);
            long diffInMillies = Math.abs(dat.getTime() - req.getModified().getTime());
            long diffDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            Map<String, Double> aux = allComponents.get(s);
            Map<String, Double> helper = new HashMap<>();
            for (String j : aux.keySet()) {
                helper.put(j, min(1.0, 1.0 - min(0.5, diffDays * (0.5 / Double.parseDouble(dropoffDays)))));
            }
            scaledKeywords.put(s, helper);
        }
        return scaledKeywords;
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


    private void instanciateResourceBatch(Map<String, Double> part, List<Participant> persons, Map<String, Requirement> recs, Map<String, Map<String, Double>> allSkills, Map<String, Set<String>> personRecs, Map<String, Integer> skillFrequency, List<String> specifiedReq, String id, Boolean withAvailability, Boolean withComponent
            , Map<String, Map<String, Double>> allComponents, Map<String, Integer> componentFrequency, String organization, Pair<Map<String, Map<String, Double>>, Map<String, Map<String, Pair<Integer, Integer>>>> pair, Map<String, Integer> loggingFrequency) throws Exception {
        List<PersonSR> toSave = new ArrayList<>();
        for (Participant person : persons) {
            List<Skill> skills;
            List<Skill> components;
            if (personRecs.get(person.getPerson()) != null) {
                skills = computeSkillsPerson(personRecs.get(person.getPerson()), allSkills, skillFrequency, pair, person.getPerson(), loggingFrequency);
                if (withComponent)
                    components = computeComponentsPerson(personRecs.get(person.getPerson()), allComponents, componentFrequency, person.getPerson());
                else components = new ArrayList<>();
            } else {
                skills = new ArrayList<>();
                components = new ArrayList<>();
            }
            Double availability;
            if (withAvailability) {
                Double hours = hoursDefault;
                if (part.containsKey(person.getPerson()) && part.get(person.getPerson()) != null)
                    hours = part.get(person.getPerson());
                if (!personRecs.containsKey(person.getPerson())) availability = 1.0;
                else
                    availability = computeAvailability(specifiedReq, personRecs, person, recs, id, hours, organization);
            } else availability = 1.0;
            PersonSR per = new PersonSR(new PersonSRId(id, person.getPerson(), organization), id, availability, skills, organization);
            if (part.containsKey(per.getName()) && part.get(per.getName()) != null)
                per.setHours(part.get(per.getName()));
            else per.setHours(hoursDefault);
            per.setComponents(components);
            toSave.add(per);
        }
        PersonSRRepository.saveAll(toSave);
    }

    private List<Skill> computeComponentsPerson(Set<String> oldRecs, Map<String, Map<String, Double>> allComponents, Map<String, Integer> componentFrequency, String s) throws JsonProcessingException {
        return getSkills(oldRecs, allComponents, componentFrequency, null, s, null);

    }

    private List<Skill> getSkills(Set<String> oldRecs, Map<String, Map<String, Double>> allComponents, Map<String, Integer> componentFrequency,
                                  Pair<Map<String, Map<String, Double>>, Map<String, Map<String, Pair<Integer, Integer>>>> pair, String person, Map<String, Integer> loggingFrequency) throws JsonProcessingException {
        List<Skill> toret = new ArrayList<>();
        Map<String, SinglePair<Double>> appearances = getAppearances(oldRecs, allComponents, componentFrequency);
        Pair<Map<String, SinglePair<Double>>, Map<String, Pair<Integer, Integer>>> appearancesLog = null;
        if (loggingFrequency != null) {
            if (pair.getSecond().containsKey(person))
                appearancesLog = getAppearancesWithTime(pair.getSecond().get(person), pair.getFirst(), loggingFrequency);
        }
        if (pair != null && appearancesLog != null) {
            for (String n : appearancesLog.getFirst().keySet()) {
                if (!appearances.containsKey(n)) appearances.put(n, new SinglePair<>(0.0, 0.0));
            }
            for (String key : appearances.keySet()) {
                Double ability = calculateWeight(appearances.get(key).p2, appearances.get(key).p1);
                Double trueAbility = calculateWeightWithLogging(key, appearancesLog);
                ability = ability * 0.6 + trueAbility;
                Skill helper = new Skill(key, ability);
                toret.add(helper);
            }
        } else {
            for (String key : appearances.keySet()) {
                Double ability = calculateWeight(appearances.get(key).p2, appearances.get(key).p1);
                Skill helper = new Skill(key, ability);
                toret.add(helper);
            }

        }
        return toret;
    }

    private Pair<Map<String, SinglePair<Double>>, Map<String, Pair<Integer, Integer>>> getAppearancesWithTime(Map<String, Pair<Integer, Integer>> stringPairMap, Map<String, Map<String, Double>> first, Map<String, Integer> loggingFrequency) throws JsonProcessingException {
        Pair<Map<String, SinglePair<Double>>, Map<String, Pair<Integer, Integer>>> res;
        Map<String, SinglePair<Double>> appearances = new HashMap<>();
        Map<String, Pair<Integer, Integer>> times = new HashMap<>();

        for (String s : stringPairMap.keySet()) {
            Map<String, Double> help = first.get(s);
            ObjectMapper mapper = new ObjectMapper();
            for (String sk : help.keySet()) {
                addAppearance(loggingFrequency, appearances, help, sk);
                if (times.containsKey(sk)) {
                    times.put(sk, new Pair<>(times.get(sk).getFirst() + stringPairMap.get(s).getFirst(), times.get(sk).getSecond() + stringPairMap.get(s).getSecond()));
                } else {
                    times.put(sk, new Pair<>(stringPairMap.get(s).getFirst(), stringPairMap.get(s).getSecond()));
                }
            }
        }

        res = new Pair<>(appearances, times);
        return res;
    }

    private Double calculateWeightWithLogging(String key, Pair<Map<String, SinglePair<Double>>, Map<String, Pair<Integer, Integer>>> appearancesAndTimes) {

        Map<String, SinglePair<Double>> appearances = appearancesAndTimes.getFirst();
        Map<String, Pair<Integer, Integer>> times = appearancesAndTimes.getSecond();
        Double editValue = 0.0, viewValue = 0.0;
        if (times.containsKey(key)) editValue = (double) times.get(key).getFirst();
        if (times.containsKey(key)) viewValue = (double) times.get(key).getSecond();
        Double view = -1.0;
        Double edit = -1.0;
        if (editValue != 0.0) {
            edit = appearances.get(key).p1 / appearances.get(key).p2;
            edit = edit * 0.7 + (editValue / 100) * 0.3;
        }
        if (viewValue != 0.0) {
            view = appearances.get(key).p1 / appearances.get(key).p2;
            view = view * 0.7 + (viewValue / 100) * 0.3;
        }
        Double retValue = 0.0;
        if (view != -1.0) {
            retValue = retValue + view * 0.1;
        }
        if (edit != -1.0) {
            retValue = retValue + edit * 0.3;
        }
        return retValue;
    }

    private Map<String, SinglePair<Double>> getAppearances(Set<String> oldRecs, Map<String, Map<String, Double>> allComponents, Map<String, Integer> componentFrequency) {
        Map<String, SinglePair<Double>> appearances = new HashMap<>();
        for (String s : oldRecs) {
            Map<String, Double> help = allComponents.get(s);
            for (String sk : help.keySet()) {
                addAppearance(componentFrequency, appearances, help, sk);
            }
        }
        return appearances;
    }

    private void addAppearance(Map<String, Integer> componentFrequency, Map<String, SinglePair<Double>> appearances, Map<String, Double> help, String sk) {
        if (appearances.containsKey(sk)) {
            SinglePair<Double> aux = appearances.get(sk);
            Double auxi = aux.p1 + help.get(sk);
            appearances.put(sk, new SinglePair<>(auxi, aux.p2));
        } else {
            appearances.put(sk, new SinglePair<>(help.get(sk), (double) componentFrequency.get(sk)));
        }
    }

    private Double computeAvailability(List<String> recs, Map<String, Set<String>> personRecs, Participant person, Map<String, Requirement> requirementMap, String project, Double totalHours, String organization) throws Exception {
        Set<String> requirements = personRecs.get(person.getPerson());
        List<String> intersection = new ArrayList<>(requirements);
        List<String> toRemove = new ArrayList<>(requirements);
        if (recs != null) {
            toRemove.removeAll(recs);
            intersection.removeAll(toRemove);
        } else intersection = null;
        Double hours = 0.0;
        if (intersection != null) {
            for (String s : intersection) {
                hours += extractAvailability(requirementMap.get(s).getEffort(), project, organization);
            }
        }
        return calculateAvailability(hours, totalHours);
    }

    private Double calculateAvailability(Double hours, Double i) {
        return max(0, (1 - (hours / i)));
    }

    private Double extractAvailability(Double s, String project, String organization) throws Exception {
        if (EffortRepository.findById(new ProjectSRId(project, organization)) == null) {
            throw new Exception();
        }
        Effort eff = EffortRepository.findById(new ProjectSRId(project, organization));
        return eff.getEffortMap().get(s);
    }

    private void instanciateFeatureBatch(List<String> requirement, String id, Map<String, Map<String, Double>> keywordsForReq, Map<String, Requirement> recs, Boolean withComponent, Map<String, Map<String, Double>> allComponents, String organization) {
        List<RequirementSR> reqs = new ArrayList<>();
        if (requirement != null) {
            for (String rec : requirement) {
                RequirementSR req = new RequirementSR(recs.get(rec), id, organization);
                ArrayList<String> aux = new ArrayList<>(keywordsForReq.get(rec).keySet());
                req.setSkills(aux);
                if (withComponent) req.setComponent(new ArrayList<>(allComponents.get(rec).keySet()));
                reqs.add(req);
            }
        }
        RequirementSRRepository.saveAll(reqs);
    }

    private String instanciateProject(Project proj, List<Participant> participants, String organization, Boolean rake, Integer size, Boolean bugzilla) {
        String id = proj.getId();
        ProjectSR projectSRTrad = new ProjectSR(new ProjectSRId(proj.getId(), organization));
        List<String> parts = new ArrayList<>();
        for (Participant par : participants) {
            parts.add(par.getPerson());
        }
        projectSRTrad.setBugzilla(bugzilla);
        projectSRTrad.setRecSize(size);
        projectSRTrad.setParticipants(parts);
        projectSRTrad.setRake(rake);
        ProjectRepository.save(projectSRTrad);
        return id;
    }


    private Map<String, Map<String, Double>> computeAllSkillsRequirement(Map<String, Requirement> recs, String organization) throws IOException {
        TFIDFKeywordExtractor extractor = new TFIDFKeywordExtractor();
        //Extract map with (Requirement / KeywordValue)
        Map<String, Map<String, Double>> keywords = extractor.computeTFIDF(recs.values());
        Date dat = new Date();

        //Transform the map from (Requirement / KeywordValue) to (Requirement / SkillFactor)

        //Skill factor is a linear function, dropping off lineally up to 0.5, based on the days
        //since the requirement was last touched
        HashMap<String, Integer> mod = extractor.getCorpusFrequency();
        KeywordExtractionModel toSave = new KeywordExtractionModel();
        toSave.setModel(mod);
        toSave.setId(organization);
        KeywordExtractionModelRepository.save(toSave);
        KeywordExtractionModelRepository.flush();
        return computeTimeFactor(recs, keywords, dat);
    }

    private Map<String, Map<String, Double>> computeAllSkillsRequirementRAKE(Map<String, Requirement> recs, String organization) throws IOException {
        //Extract map with (Requirement / KeywordValue)
        Map<String, Map<String, Double>> keywords = new RAKEKeywordExtractor().computeRake(recs.values());
        Date dat = new Date();

        //Transform the map from (Requirement / KeywordValue) to (Requirement / SkillFactor)

        //Skill factor is a linear function, dropping off lineally up to 0.5, based on the days
        //since the requirement was last touched
        return computeTimeFactor(recs, keywords, dat);
    }


    private List<Skill> computeSkillsPerson(Set<String> oldRecs, Map<String, Map<String, Double>> recs, Map<String, Integer> skillsFrequency, Pair<Map<String, Map<String, Double>>, Map<String, Map<String, Pair<Integer, Integer>>>> pair, String s, Map<String, Integer> loggingFrequency) throws JsonProcessingException {
        return getSkills(oldRecs, recs, skillsFrequency, pair, s, loggingFrequency);
    }

    private Double calculateWeight(Double appearances, Double requirement) {
        if (appearances == 0) return 0.0;
        else return requirement / appearances;
    }

    private Map<String, Set<String>> getPersonRecs(BatchSchema request) {
        Map<String, Set<String>> personRecs = new HashMap<>();
        for (Responsible resp : request.getResponsibles()) {
            if (personRecs.containsKey(resp.getPerson())) {
                Set<String> aux = personRecs.get(resp.getPerson());
                aux.add(resp.getRequirement());
                personRecs.put(resp.getPerson(), aux);
            } else {
                Set<String> aux = new HashSet<>();
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

    public List<ProjectKeywordSchema> extractKeywords(String organization, BatchSchema batch) {
        List<ProjectKeywordSchema> res = new ArrayList<>();
        for (Project j : batch.getProjects()) {
            ProjectKeywordSchema proje = new ProjectKeywordSchema();
            List<KeywordReturnSchema> reqs = new ArrayList<>();
            String id = j.getId();
            proje.setProjectId(id);
            for (RequirementSR req : RequirementSRRepository.findByOrganizationAndProj(organization, id)) {
                KeywordReturnSchema key = new KeywordReturnSchema();
                key.setRequirement(req.getId().getRequirementId());
                key.setSkills(new ArrayList<>(req.getSkillsSet()));
                reqs.add(key);
            }
            proje.setRequirements(reqs);
            res.add(proje);
        }
        return res;
    }

    public void undo_recommend_reject(String rejected, String user, String requirement, String organization) {
        RejectedPerson rej = RejectedPersonRepository.findByUser(new RejectedPersonId(user, organization));
        Map<String, HashSet<String>> deleted = rej.getDeleted();
        HashSet set = deleted.get(rejected);
        set.remove(requirement);
        deleted.put(rejected, set);
        rej.setDeleted(deleted);
        RejectedPersonRepository.save(rej);
    }

    public List<Skill> getPersonSkills(String person, String organization, Integer k) {
        List<PersonSR> per = PersonSRRepository.findByNameAndOrganization(person, organization);
        if (per != null && per.size() > 0) {
            PersonSR truePerson = per.get(0);
            List<Skill> skill = truePerson.getSkills();
            Collections.sort(skill,
                    Comparator.comparingDouble(Skill::getWeight).reversed());
            List<Skill> newList = new ArrayList<>();
            if (k != -1) {
                if (k > skill.size()) k = skill.size();
                for (int i = 0; i < k; ++i) {
                    newList.add(skill.get(i));
                }
            } else newList = skill;
            return newList;
        } else return null;
    }


    private class SinglePair<T> {
        T p1, p2;

        SinglePair(T p1, T p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

    }

    private Pair<Map<String, Map<String, Double>>, Map<String, Map<String, Pair<Integer, Integer>>>> getUserLogging(Boolean bugzilla, Boolean rake, String organization, Integer size, Integer test) throws GeneralSecurityException, IOException {
        LogArray log = null;
        if (test == 0) {
            SslContextUtils.mergeWithSystem("cert/lets_encrypt.jks");
            RestTemplate temp = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth("7kyT5sGL8y5ax6qHJU32L4CJ");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<LogArray> res = temp.exchange("https://api.openreq.eu/ri-logging/frontend/log", HttpMethod.GET, entity, LogArray.class);
            log = res.getBody();
        } else {
            ObjectMapper map = new ObjectMapper();
            File file = new File("src/main/resources/testingFiles/RiLoggingResponse.txt");
            String jsonInString = null;
            jsonInString = FileUtils.readFileToString(file, StandardCharsets.US_ASCII);
            log = map.readValue(jsonInString, LogArray.class);
        }

        return log(log.getLogs(), bugzilla, rake, organization, size, test);
    }

    public Pair<Map<String, Map<String, Double>>, Map<String, Map<String, Pair<Integer, Integer>>>> log(List<Log> logList, Boolean bugzilla, Boolean rake, String organization, Integer size, Integer test) throws IOException {
        Map<String, List<Log>> logged = new HashMap<>();
        if (logList != null)
            for (Log l : logList) {
                if (l.getBody() != null && l.getBody().getUsername() != null && l.getBody().getRequirementId() != null) {
                    if (!logged.containsKey(l.getBody().getUsername())) {
                        ArrayList<Log> list = new ArrayList<>();
                        list.add(l);
                        logged.put(l.getBody().getUsername(), list);
                    } else {
                        List<Log> list = logged.get(l.getBody().getUsername());
                        list.add(l);
                        logged.put(l.getBody().getUsername(), list);
                    }
                }
            }
        Map<String, Map<String, Pair<Integer, Integer>>> timesForReq = new HashMap<>();
        Map<String, List<Log>> reqId = new HashMap<>();
        for (String s : logged.keySet()) {
            List<Log> toOrder = logged.get(s);
            Collections.sort(toOrder,
                    Comparator.comparingInt(Log::getUnixTime));
            for (Log l : toOrder) {
                if (reqId.containsKey(l.getBody().getRequirementId())) {
                    List<Log> auxList = reqId.get(l.getBody().getRequirementId());
                    auxList.add(l);
                    reqId.put(l.getBody().getRequirementId(), auxList);
                } else {
                    List<Log> auxList = new ArrayList<>();
                    auxList.add(l);
                    reqId.put(l.getBody().getRequirementId(), auxList);

                }
            }
            Map<String, Pair<Integer, Integer>> times = extractTimeInRequirement(toOrder);
            logged.put(s, toOrder);
            timesForReq.put(s, times);
        }
        Map<String, Requirement> trueRecs = new HashMap<>();
        for (String s : reqId.keySet()) {
            List<Log> toOrder = reqId.get(s);
            Collections.sort(toOrder,
                    Comparator.comparingInt(Log::getUnixTime));
            Requirement req = new Requirement();
            req.setId(s);
            for (int i = toOrder.size() - 1; i >= 0; --i) {
                Log lo = toOrder.get(i);
                if (req.getName() == null && req.getDescription() == null) {
                    req.setModified(new Date(lo.getUnixTime() * (long) 1000));
                } else if (req.getName() != null && req.getDescription() != null) break;
                if (req.getName() == null && lo.isName()) {
                    req.setName(lo.getDescriptionOrName());
                } else if (req.getDescription() == null && lo.isDescription()) {
                    req.setDescription(lo.getDescriptionOrName());
                }
            }
            if (req.getDescription() == null) req.setDescription("");
            if (req.getName() == null) req.setName("");
            trueRecs.put(s, req);
            reqId.put(s, toOrder);
        }
        Map<String, Map<String, Double>> skills = obtainSkills(trueRecs, bugzilla, rake, organization, size, test);
        skills = computeTime(skills, trueRecs);
        return new Pair<>(skills, timesForReq);
    }


    private Map<String, Map<String, Double>> computeTime(Map<String, Map<String, Double>> skills, Map<String, Requirement> trueRecs) {
        skills = computeTimeFactor(trueRecs, skills, new Date());
        return skills;
    }


    private Map<String, Map<String, Double>> obtainSkills(Map<String, Requirement> trueRecs, Boolean bugzilla, Boolean rake, String organization, Integer size, Integer test) throws IOException {
        Map<String, Map<String, Double>> map;
        if (rake) {
            map = new RAKEKeywordExtractor().computeRake(trueRecs.values());
        } else if (bugzilla) {
            Collection<Requirement> col = trueRecs.values();
            List<Requirement> toMakeSkill = Preprocess.preprocess(new ArrayList<>(col), test);
            for (Requirement r : toMakeSkill) {
                trueRecs.put(r.getId(), r);
            }
            map = computeAllSkillsNoMethod(trueRecs);
        } else {
            Map<String, Integer> model = KeywordExtractionModelRepository.getOne(organization).getModel();
            map = new TFIDFKeywordExtractor().computeTFIDFExtra(model, size, trueRecs);
            KeywordExtractionModel mod = new KeywordExtractionModel();
            mod.setId(organization);
            mod.setModel(model);
            KeywordExtractionModelRepository.save(mod);
        }
        return map;
    }

    private Map<String, Pair<Integer, Integer>> extractTimeInRequirement(List<Log> toOrder) throws JsonProcessingException {
        String currentSessionId = "";
        String lastElement = "";
        String lastType = "";
        String lastValue = "";
        String lastInnertext="";
        Integer lastTime = 0;
        Map<String, Pair<Integer, Integer>> toRet = new HashMap<>();
        for (Log l : toOrder) {
            //String newSessionId=l.getHeader().getSessionid();
            // if (currentSessionId.equals(newSessionId)) {
            String newType = l.getEvent_type();
            if ((lastElement.equals(l.getBody().getSrcElementclassName())||(lastElement.equals("note-editable")&&l.getBody().getSrcElementclassName().equals("note-editable or-description-active"))
                    ||(lastElement.equals("note-editable or-description-active")&&l.getBody().getSrcElementclassName().equals("note-editable")))&& lastType.equals("focus") && newType.equals("blur")) {
                Integer time = l.getUnixTime() - lastTime;
                if (toRet.containsKey(l.getBody().getRequirementId())) {
                    if (edited(lastValue,lastInnertext, l)) {
                        toRet.put(l.getBody().getRequirementId(), new Pair<>(time + toRet.get(l.getBody().getRequirementId()).getFirst(), toRet.get(l.getBody().getRequirementId()).getSecond()));
                    } else {
                        toRet.put(l.getBody().getRequirementId(), new Pair<>(toRet.get(l.getBody().getRequirementId()).getFirst(), toRet.get(l.getBody().getRequirementId()).getSecond() + time));
                    }
                } else {
                    if (edited(lastValue,lastInnertext, l)) {
                        toRet.put(l.getBody().getRequirementId(), new Pair<>(time, 0));
                    } else {
                        toRet.put(l.getBody().getRequirementId(), new Pair<>(0, time));
                    }
                }
                //   }
            }
            lastTime = l.getUnixTime();
            lastType = l.getEvent_type();
            lastElement = l.getBody().getSrcElementclassName();
            lastValue = l.getBody().getValue();
            lastInnertext=l.getBody().getInnerText();
        }
        return toRet;
    }

    private boolean edited(String lastValue,String lastInnerText, Log l) {
        if (l.getBody().getSrcElementclassName().equals("select-dropdown")) {
            return true;
        } else if (l.getBody().getSrcElementclassName().equals("or-requirement-title form-control")) {
            return !lastValue.equals(l.getBody().getValue());
        }
        else if (l.getBody().getSrcElementclassName().equals("note-editable")||l.getBody().getSrcElementclassName().equals("note-editable or-description-active")) {
            return !lastInnerText.equals(l.getBody().getInnerText());
        }
       else return false;
    }




}
