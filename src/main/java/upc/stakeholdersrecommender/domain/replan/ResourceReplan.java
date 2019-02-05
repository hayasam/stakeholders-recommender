package upc.stakeholdersrecommender.domain.replan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import upc.stakeholdersrecommender.entity.Person;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceReplan implements Serializable {

    private Integer id;
    private String name;
    private Double availability;
    private List<DaySlot> calendar;

    public ResourceReplan() {

    }

    public ResourceReplan(Person p) {
        this.name = p.getUsername();
        this.availability = 100.00;
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

    public Double getAvailability() {
        return availability;
    }

    public void setAvailability(Double availability) {
        this.availability = availability;
    }

    public List<DaySlot> getCalendar() {
        return calendar;
    }

    public void setCalendar(List<DaySlot> calendar) {
        this.calendar = calendar;
    }
}
