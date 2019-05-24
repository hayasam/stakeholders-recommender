package upc.stakeholdersrecommender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import upc.stakeholdersrecommender.entity.PersonSR;
import upc.stakeholdersrecommender.entity.PersonSRId;

import java.util.List;

@Repository
public interface PersonSRRepository extends JpaRepository<PersonSR, String> {
    List<PersonSR> findByProjectIdQuery(String projectId);

    PersonSR findById(PersonSRId id);


    @Transactional
    void deleteByProjectIdQuery(String projectId);

    List<PersonSR> findByName(String name);
}
