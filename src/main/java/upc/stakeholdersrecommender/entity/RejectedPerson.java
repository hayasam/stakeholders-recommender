package upc.stakeholdersrecommender.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Set;

@Entity
@Table(name = "rejected")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RejectedPerson {

    @Id
    private String user;
    private HashMap<String, Set<String>> deleted;

    public RejectedPerson() {
        user = null;
        deleted = null;
    }

    public RejectedPerson(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public HashMap<String, Set<String>> getDeleted() {
        return deleted;
    }

    public void setDeleted(HashMap<String, Set<String>> deleted) {
        this.deleted = deleted;
    }

}
