package upc.stakeholdersrecommender.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "rejected")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RejectedPerson implements Serializable {

    @Id
    private String user;
    @ElementCollection
    private Map<String, HashSet<String>> deleted;

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

    public Map<String, HashSet<String>> getDeleted() {
        return deleted;
    }

    public void setDeleted(Map<String, HashSet<String>> deleted) {
        this.deleted = deleted;
    }

}
