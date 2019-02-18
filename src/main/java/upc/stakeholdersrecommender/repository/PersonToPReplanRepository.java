package upc.stakeholdersrecommender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import upc.stakeholdersrecommender.entity.PersonToPReplan;

@Repository
public interface PersonToPReplanRepository extends JpaRepository<PersonToPReplan, String> {
}
