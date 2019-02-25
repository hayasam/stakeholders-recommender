package upc.stakeholdersrecommender.controller;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upc.stakeholdersrecommender.domain.Responsible;
import upc.stakeholdersrecommender.domain.Schemas.BatchSchema;
import upc.stakeholdersrecommender.domain.Schemas.RejectSchema;
import upc.stakeholdersrecommender.domain.Schemas.RecommendSchema;
import upc.stakeholdersrecommender.domain.Person;
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
    public ResponseEntity addBatch(@RequestBody BatchSchema batch) {
        stakeholdersRecommenderService.addBatch(batch);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "purge", method = RequestMethod.DELETE)
    public ResponseEntity purge() {
        stakeholdersRecommenderService.purge();
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "reject_recommendation", method = RequestMethod.POST)
    public ResponseEntity recommend_reject(@RequestParam("rejected") String rejected,@RequestParam("user") String user,@RequestParam("requirement") String requirement) {
        stakeholdersRecommenderService.recommend_reject(rejected,user,requirement);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "recommend", method = RequestMethod.POST)
    public ResponseEntity<List<Responsible>> recommend(@RequestBody RecommendSchema request) {
        List<Responsible> ret=stakeholdersRecommenderService.recommend(request);
        return new ResponseEntity<>(ret,HttpStatus.OK);
    }

}
