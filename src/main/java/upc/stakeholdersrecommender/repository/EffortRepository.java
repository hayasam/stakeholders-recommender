package upc.stakeholdersrecommender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upc.stakeholdersrecommender.entity.Effort;

public interface EffortRepository extends JpaRepository<Effort, String> {
}
