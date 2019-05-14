package upc.stakeholdersrecommender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import upc.stakeholdersrecommender.entity.RequirementReplanId;

@Repository
public interface RequirementReplanRepository extends JpaRepository<upc.stakeholdersrecommender.entity.RequirementReplan, String> {
    upc.stakeholdersrecommender.entity.RequirementReplan findById(RequirementReplanId id);

    upc.stakeholdersrecommender.entity.RequirementReplan findByIdReplan(String IdReplan);

    @Transactional
    void deleteByProjectIdQuery(String projectId);

}
