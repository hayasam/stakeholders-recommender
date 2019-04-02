package upc.stakeholdersrecommender.domain.replan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import upc.stakeholdersrecommender.domain.Person;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

    public ResourceReplan(Person p, Double availability) {
        this.name = p.getUsername();
        this.availability = availability;
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

    public Set<String> getFeaturesWorkedOn() {
        TreeSet<String> result = new TreeSet<String>();
        if (calendar != null)
            for (DaySlot day : calendar) {
                if (day.getFeature_id() != null) {
                    System.out.println(day.getFeature_id());
                    result.add(day.getFeature_id());
                }
            }
        return result;
    }
}
