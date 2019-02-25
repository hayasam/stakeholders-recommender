package upc.stakeholdersrecommender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import upc.stakeholdersrecommender.entity.RequirementToFeature;

@Repository
public interface RequirementToFeatureRepository extends JpaRepository<RequirementToFeature, String> {
    RequirementToFeature findByIdReplan(Integer IdReplan);

    @Transactional
    void deleteByProjectIdQuery(Integer projectId);

}
