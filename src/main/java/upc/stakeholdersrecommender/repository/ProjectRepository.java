package upc.stakeholdersrecommender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import upc.stakeholdersrecommender.entity.ProjectSR;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectSR, String> {

    @Transactional
    void deleteByOrganization(String organization);


}
