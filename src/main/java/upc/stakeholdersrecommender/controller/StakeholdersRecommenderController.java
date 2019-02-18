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
import upc.stakeholdersrecommender.domain.RecommendRejectSchema;
import upc.stakeholdersrecommender.domain.RecommendSchema;
import upc.stakeholdersrecommender.entity.Person;
import upc.stakeholdersrecommender.entity.ReturnObject;
import upc.stakeholdersrecommender.service.StakeholdersRecommenderService;

import java.util.List;

@SuppressWarnings("ALL")
@RestController
@RequestMapping("/upc/stakeholders-recommender")
@Api(value = "Stakeholders Recommender API", produces = MediaType.APPLICATION_JSON_VALUE)
public class StakeholdersRecommenderController {

    @Autowired
    StakeholdersRecommenderService stakeholdersRecommenderService;

    private static final Logger logger = LoggerFactory.getLogger(StakeholdersRecommenderController.class);

    @RequestMapping(value = "batch_process", method = RequestMethod.POST)
    public ResponseEntity addBatch(@RequestBody OpenReqSchema batch) {
        stakeholdersRecommenderService.addBatch(batch);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "purge", method = RequestMethod.GET)
    public ResponseEntity addBatch() {
        stakeholdersRecommenderService.purge();
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "reject_recommendation", method = RequestMethod.POST)
    public ResponseEntity<Person> recommend_reject(@RequestBody RecommendRejectSchema request) {
        stakeholdersRecommenderService.recommend_reject(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "recommend", method = RequestMethod.POST)
    public ResponseEntity recommend(@RequestBody RecommendSchema request) {
        List<ReturnObject> ret=stakeholdersRecommenderService.recommend(request);
        return new ResponseEntity<>(ret,HttpStatus.OK);
    }

}
