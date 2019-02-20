package upc.stakeholdersrecommender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import upc.stakeholdersrecommender.entity.RequirementToFeature;

@Repository
public interface RequirementToFeatureRepository extends JpaRepository<RequirementToFeature, String> {
    public RequirementToFeature findByIdReplan(Integer IdReplan);
    @Transactional
    public void deleteByProjectIdQuery(Integer projectId);

}
