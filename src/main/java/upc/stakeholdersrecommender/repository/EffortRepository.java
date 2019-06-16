package upc.stakeholdersrecommender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import upc.stakeholdersrecommender.entity.*;

@Repository
public interface EffortRepository extends JpaRepository<Effort, String> {

    Effort findById(ProjectSRId id);

    @Transactional
    void deleteById(ProjectSRId id);


}
