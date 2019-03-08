package upc.stakeholdersrecommender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import upc.stakeholdersrecommender.entity.RequirementSkills;

@Repository
public interface RequirementSkillsRepository extends JpaRepository<RequirementSkills, String> {
    @Transactional
    void deleteByProjectIdQuery(String projectId);

}
