package upc.stakeholdersrecommender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upc.stakeholdersrecommender.entity.Skill;

public interface SkillRepository extends JpaRepository<Skill, String> {

}
