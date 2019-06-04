package upc.stakeholdersrecommender.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upc.stakeholdersrecommender.domain.Responsible;
import upc.stakeholdersrecommender.domain.Schemas.*;
import upc.stakeholdersrecommender.service.EffortCalculator;
import upc.stakeholdersrecommender.service.StakeholdersRecommenderService;

import java.io.IOException;
import java.util.List;


@SuppressWarnings("ALL")
@RestController
@RequestMapping("/upc/stakeholders-recommender")
@Api(value = "Stakeholders Recommender API", produces = MediaType.APPLICATION_JSON_VALUE)
public class StakeholdersRecommenderController {

    private static final Logger logger = LoggerFactory.getLogger(StakeholdersRecommenderController.class);
    @Autowired
    StakeholdersRecommenderService stakeholdersRecommenderService;
    @Autowired
    EffortCalculator effortCalc;


    @RequestMapping(value = "batch_process", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Batch process request to upload required data for stakeholder recommendation." +
            " \n The parameter withAvailability specifies whether a availability is calculated based on the stakeholder's past history" +
            " or not. All information in the database is purged every time this method is called. A person's relation to the project is defined with" +
            "PARTICIPANT (availability is expressed in hours), while the person is defined in PERSONS, the requirements in REQUIREMENTS, the project in PROJECTS, and a person's" +
            "relation to a requirement in RESPONSIBLES.", notes = "", response = BatchReturnSchema.class)
    public ResponseEntity<BatchReturnSchema> addBatch(@RequestBody BatchSchema batch, @ApiParam(value = "Whether the recommendation take into account the stakeholder's availability or not.", example = "false")
    @RequestParam Boolean withAvailability) throws Exception {
        int res = 0;
        try {
            res = stakeholdersRecommenderService.addBatch(batch, withAvailability);
        } catch (IOException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<BatchReturnSchema>(new BatchReturnSchema(res), HttpStatus.CREATED);
    }


    @RequestMapping(value = "reject_recommendation", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Recommendation rejection method: used to state that the user identified by REJECTED must not be assigned to REQUIREMENT. The" +
            " rejection is performed by USER.", notes = "")
    public ResponseEntity recommend_reject(@ApiParam(value = "Person who is rejected.", example = "Not JohnDoe")@RequestParam("rejected") String rejected,@ApiParam(value = "Person who rejects.", example = "JohnDoe") @RequestParam("user") String user,@ApiParam(value = "From which requirement is the person rejected.", example = "1") @RequestParam("requirement") String requirement) {
        stakeholdersRecommenderService.recommend_reject(rejected, user, requirement);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "recommend", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Given a REQUIREMENT in a PROJECT, asked by a USER, the Stakeholder Recommender service performs a " +
            "recommendation and returns a list of the best K stakeholders based on the historic data given in the batch_process." +
            "\n The parameter projectSpecific specifies if the recommendation takes into account all stakeholders given in the batch_process, or only those" +
            " specified in \"PARTICIPANTS\", in the batch_process", notes = "", response = RecommendReturnSchema[].class)
    public ResponseEntity<List<RecommendReturnSchema>> recommend(@RequestBody RecommendSchema request,
                                                                 @ApiParam(value = "Returns the top k stakeholders.", example = "10")@RequestParam Integer k,@ApiParam(value = "Considers stakeholders from all projects or only from one.", example = "false") @RequestParam Boolean projectSpecific) throws Exception {
        List<RecommendReturnSchema> ret = stakeholdersRecommenderService.recommend(request, k, projectSpecific);
        return new ResponseEntity<List<RecommendReturnSchema>>(ret, HttpStatus.CREATED);
    }

    @RequestMapping(value = "setEffort", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Set the mapping of effort points into hours, the effort points go in a scale from 1 to 5, the effort is specific to a project", notes = "")
    public ResponseEntity setEffort(@RequestBody SetEffortSchema eff,@ApiParam(value = "Which project this effort is refering to.", example = "1") @RequestParam String project) throws IOException {
        effortCalc.setEffort(eff, project);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "computeEffort", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Generate a mapping of effort points into hours specific to the project specified, based in the historic information given" +
            ",a list of hours per effort point, based on the time a requirement with those effort points required to be finished. The effort points go" +
            "in a scale from 1 to 5", notes = "")
    public ResponseEntity calculateEffort(@RequestBody EffortCalculatorSchema eff,@ApiParam(value = "Which project this effort is refering to.", example = "1") @RequestParam String project) throws IOException {
        effortCalc.effortCalc(eff, project);
        return new ResponseEntity(HttpStatus.CREATED);
    }


}
