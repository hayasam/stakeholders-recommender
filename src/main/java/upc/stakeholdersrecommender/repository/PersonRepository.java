package upc.stakeholdersrecommender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upc.stakeholdersrecommender.entity.Person;

public interface PersonRepository extends JpaRepository<Person, String> {
}
