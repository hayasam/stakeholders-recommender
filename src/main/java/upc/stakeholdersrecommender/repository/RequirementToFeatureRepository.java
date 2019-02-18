package upc.stakeholdersrecommender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import upc.stakeholdersrecommender.entity.Requirement;
import upc.stakeholdersrecommender.entity.RequirementToFeature;

@Repository
public interface RequirementToFeatureRepository extends JpaRepository<RequirementToFeature, String> {
    public RequirementToFeature findByIdReplan(Integer IdReplan);

}
