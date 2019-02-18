package upc.stakeholdersrecommender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import upc.stakeholdersrecommender.entity.RejectedPerson;

@Repository
public interface RejectedPersonRepository extends JpaRepository<RejectedPerson, String> {
}
