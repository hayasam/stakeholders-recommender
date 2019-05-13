package upc.stakeholdersrecommender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import upc.stakeholdersrecommender.entity.PersonId;
import upc.stakeholdersrecommender.entity.PersonToPReplan;

import java.util.List;

@Repository
public interface PersonToPReplanRepository extends JpaRepository<PersonToPReplan, String> {
    List<PersonToPReplan> findByProjectIdQuery(String projectId);

    PersonToPReplan findById(PersonId id);

    PersonToPReplan findByIdReplan(String id);



    @Transactional
    void deleteByProjectIdQuery(String projectId);

    List<PersonToPReplan> findByName(String name);
}
