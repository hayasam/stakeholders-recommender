package upc.stakeholdersrecommender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import upc.stakeholdersrecommender.entity.ProjectToPReplan;

@Repository
public interface ProjectToPReplanRepository extends JpaRepository<ProjectToPReplan, String> {

}
