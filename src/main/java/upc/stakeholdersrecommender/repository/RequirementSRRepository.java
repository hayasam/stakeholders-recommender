package upc.stakeholdersrecommender.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import upc.stakeholdersrecommender.entity.RequirementSR;
import upc.stakeholdersrecommender.entity.RequirementSRId;

@Repository
public interface RequirementSRRepository extends JpaRepository<RequirementSR, String> {
    RequirementSR findById(RequirementSRId id);

    @Transactional
    void deleteByOrganization(String organization);

    List<RequirementSR> findByOrganization(String organization);
    List<RequirementSR> findByOrganizationAndProj(String organization,String id);

}
