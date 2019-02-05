package upc.stakeholdersrecommender.domain.replan;

import upc.stakeholdersrecommender.entity.Project;

import java.io.Serializable;

public class ProjectReplan implements Serializable {

    private Integer id;
    private String name;
    private String description;
    private String effort_unit;
    private Integer hours_per_effort_unit;
    private Integer hours_per_week_and_full_time_resource;

    public ProjectReplan() {

    }

    public ProjectReplan(Project p) {
        this.name = p.getName();
        this.effort_unit = "hours";
        this.hours_per_effort_unit = 1;
        this.hours_per_week_and_full_time_resource = 40;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEffort_unit() {
        return effort_unit;
    }

    public void setEffort_unit(String effort_unit) {
        this.effort_unit = effort_unit;
    }

    public Integer getHours_per_effort_unit() {
        return hours_per_effort_unit;
    }

    public void setHours_per_effort_unit(Integer hours_per_effort_unit) {
        this.hours_per_effort_unit = hours_per_effort_unit;
    }

    public Integer getHours_per_week_and_full_time_resource() {
        return hours_per_week_and_full_time_resource;
    }

    public void setHours_per_week_and_full_time_resource(Integer hours_per_week_and_full_time_resource) {
        this.hours_per_week_and_full_time_resource = hours_per_week_and_full_time_resource;
    }
}
