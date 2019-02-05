package upc.stakeholdersrecommender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import upc.stakeholdersrecommender.entity.Person;
import upc.stakeholdersrecommender.entity.Requirement;
import upc.stakeholdersrecommender.entity.Skill;
import upc.stakeholdersrecommender.repository.PersonRepository;
import upc.stakeholdersrecommender.repository.RequirementRepository;
import upc.stakeholdersrecommender.repository.SkillRepository;

import java.util.Arrays;

@SpringBootApplication
public class StakeholdersRecommenderApplication {

	public static void main(String[] args) {
		SpringApplication.run(StakeholdersRecommenderApplication.class, args);
	}

}

