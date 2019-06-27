package upc.stakeholdersrecommender.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import upc.stakeholdersrecommender.entity.RequirementSR;
import upc.stakeholdersrecommender.entity.RequirementSRId;

import javax.persistence.QueryHint;

@Repository
public interface RequirementSRRepository extends JpaRepository<RequirementSR, String> {
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value   ="50000")})
    RequirementSR findById(RequirementSRId id);
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value   ="50000")})
    @Transactional
    void deleteByOrganization(String organization);
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value   ="50000")})
    List<RequirementSR> findByOrganization(String organization);
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value   ="50000")})
    List<RequirementSR> findByOrganizationAndProj(String organization,String id);

}
