package upc.stakeholdersrecommender.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import upc.stakeholdersrecommender.entity.RejectedPerson;
import upc.stakeholdersrecommender.entity.RejectedPersonId;

@Repository
public interface RejectedPersonRepository extends JpaRepository<RejectedPerson, String> {

    RejectedPerson findByUser(RejectedPersonId id);
    @Transactional
    void deleteByOrganization(String organization);


    List<RejectedPerson> findByOrganization(String organization);
}
