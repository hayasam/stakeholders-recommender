package upc.stakeholdersrecommender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import upc.stakeholdersrecommender.entity.PersonId;
import upc.stakeholdersrecommender.entity.PersonToPReplan;

import java.util.List;

@Repository
public interface PersonToPReplanRepository extends JpaRepository<PersonToPReplan, String> {
    public List<PersonToPReplan> findByProjectIdQuery(Integer projectId);
    public PersonToPReplan findById(PersonId id);
    public PersonToPReplan findByIdReplan(Integer id);
    @Transactional
    public void deleteByProjectIdQuery(Integer projectId);

}
