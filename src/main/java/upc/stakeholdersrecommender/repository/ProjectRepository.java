package upc.stakeholdersrecommender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import upc.stakeholdersrecommender.entity.ProjectSR;
import upc.stakeholdersrecommender.entity.ProjectSRId;

import javax.persistence.QueryHint;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectSR, String> {
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value   ="50000")})
    @Transactional
    void deleteByOrganization(String organization);

    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value   ="50000")})
    ProjectSR findByOrganization(String organization);
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value   ="50000")})
    ProjectSR findById(ProjectSRId organization);


}
