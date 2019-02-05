package upc.stakeholdersrecommender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import upc.stakeholdersrecommender.entity.Requirement;

@Repository
public interface RequirementRepository extends JpaRepository<Requirement, String> {

}
