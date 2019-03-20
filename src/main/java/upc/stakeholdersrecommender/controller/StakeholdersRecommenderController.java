package upc.stakeholdersrecommender.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upc.stakeholdersrecommender.domain.Responsible;
import upc.stakeholdersrecommender.domain.Schemas.*;
import upc.stakeholdersrecommender.service.StakeholdersRecommenderService;

import java.io.IOException;
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
    @ApiOperation(value = "Batch process request to upload required data for stakeholder recommendation.")
    public ResponseEntity addBatch(@RequestBody BatchSchema batch) throws IOException {
        Integer res=stakeholdersRecommenderService.addBatch(batch);
        return new ResponseEntity(new BatchReturnSchema(res),HttpStatus.CREATED);
    }

    @RequestMapping(value = "purge", method = RequestMethod.DELETE)
    @ApiOperation(value = "Removes all data from the service database.")
    public ResponseEntity purge() {
        stakeholdersRecommenderService.purge();
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "reject_recommendation", method = RequestMethod.POST)
    @ApiOperation(value = "Recommendation rejection method: used to state that the user identified by REJECTED must not be assigned to REQUIREMENT. The" +
            " rejection is performed by USER.")
    public ResponseEntity recommend_reject(@RequestParam("rejected") String rejected, @RequestParam("user") String user, @RequestParam("requirement") String requirement) {
        stakeholdersRecommenderService.recommend_reject(rejected, user, requirement);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "recommend", method = RequestMethod.POST)
    @ApiOperation(value = "Given a requirement and a list of persons, the Stakeholder Recommender service performs a " +
            "recommendation and returns a list of the best K recommendations")
    public ResponseEntity<List<Responsible>> recommend(@RequestBody RecommendSchema request,
                                                       @RequestParam Integer k) {
        List<RecommendReturnSchema> ret = stakeholdersRecommenderService.recommend(request, k);
        return new ResponseEntity(ret, HttpStatus.CREATED);
    }

    @RequestMapping(value = "extractor", method = RequestMethod.POST)
    public ResponseEntity  extract(@RequestBody ExtractTest request) throws IOException {
        stakeholdersRecommenderService.extract(request);
        return new ResponseEntity<>( HttpStatus.OK);
    }
    /*
    @RequestMapping(value = "extractor2", method = RequestMethod.POST)
    public ResponseEntity  extract2(@RequestBody ExtractTest request) throws IOException {
        stakeholdersRecommenderService.extract2(request);
        return new ResponseEntity<>( HttpStatus.OK);
    }*/


}
