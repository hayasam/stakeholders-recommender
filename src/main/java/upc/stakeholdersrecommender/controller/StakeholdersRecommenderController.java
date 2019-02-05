package upc.stakeholdersrecommender.controller;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upc.stakeholdersrecommender.domain.OpenReqSchema;
import upc.stakeholdersrecommender.domain.PersonList;
import upc.stakeholdersrecommender.domain.RequirementList;
import upc.stakeholdersrecommender.entity.Person;
import upc.stakeholdersrecommender.entity.Requirement;
import upc.stakeholdersrecommender.repository.PersonRepository;
import upc.stakeholdersrecommender.repository.RequirementRepository;
import upc.stakeholdersrecommender.service.ReplanService;
import upc.stakeholdersrecommender.service.StakeholdersRecommenderService;

@RestController
@RequestMapping("/upc/stakeholders-recommender")
@Api(value = "Stakeholders Recommender API", produces = MediaType.APPLICATION_JSON_VALUE)
public class StakeholdersRecommenderController {

    @Autowired
    StakeholdersRecommenderService stakeholdersRecommenderService;

    private static final Logger logger = LoggerFactory.getLogger(StakeholdersRecommenderController.class);

    @RequestMapping(value = "requirements", method = RequestMethod.POST)
    public ResponseEntity addRequirements(@RequestBody RequirementList requirementList) {
        stakeholdersRecommenderService.addRequirements(requirementList);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "requirements/{id}", method = RequestMethod.GET)
    public ResponseEntity<Requirement> getRequirement(@PathVariable String id) {
        Requirement r = stakeholdersRecommenderService.getRequirement(id);
        return new ResponseEntity<>(r, HttpStatus.OK);
    }

    @RequestMapping(value = "persons", method = RequestMethod.POST)
    public ResponseEntity addPersons(@RequestBody PersonList personList) {
        stakeholdersRecommenderService.addPersons(personList);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "persons/{id}", method = RequestMethod.GET)
    public ResponseEntity<Person> getPerson(@PathVariable String id) {
        Person p = stakeholdersRecommenderService.getPerson(id);
        return new ResponseEntity<>(p, HttpStatus.OK);
    }

    @RequestMapping(value = "recommend", method = RequestMethod.POST)
    public ResponseEntity recommend(@RequestBody OpenReqSchema request) {
        stakeholdersRecommenderService.recommend(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
