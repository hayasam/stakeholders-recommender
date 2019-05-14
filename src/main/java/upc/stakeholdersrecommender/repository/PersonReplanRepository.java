package upc.stakeholdersrecommender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import upc.stakeholdersrecommender.entity.PersonReplanId;
import upc.stakeholdersrecommender.entity.PersonReplan;

import java.util.List;

@Repository
public interface PersonReplanRepository extends JpaRepository<PersonReplan, String> {
    List<PersonReplan> findByProjectIdQuery(String projectId);

    PersonReplan findById(PersonReplanId id);

    PersonReplan findByIdReplan(String id);



    @Transactional
    void deleteByProjectIdQuery(String projectId);

    List<PersonReplan> findByName(String name);
}
