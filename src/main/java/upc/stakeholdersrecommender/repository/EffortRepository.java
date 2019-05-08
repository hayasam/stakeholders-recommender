package upc.stakeholdersrecommender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import upc.stakeholdersrecommender.entity.Effort;

@Repository
public interface EffortRepository extends JpaRepository<Effort, String> {
}
