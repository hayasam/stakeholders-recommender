package upc.stakeholdersrecommender.domain.replan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReleaseReplan implements Serializable {

    private Integer id;
    private String name;
    private String starts_at;
    private String deadline;

    public ReleaseReplan() {

    }

    public ReleaseReplan(String name) {
        this.name = name;
        this.starts_at = "2019-02-01T10:36:32.515Z";
        this.deadline = "2019-02-28T10:36:32.515Z";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStarts_at() {
        return starts_at;
    }

    public void setStarts_at(String starts_at) {
        this.starts_at = starts_at;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}
