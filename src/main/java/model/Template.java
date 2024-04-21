package model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
public class Template {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long id;
    private DayOfWeek weekday;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;
    //current appointments are to be generated from it?
    private boolean active;

    //are multiple appointments for the same time slot possible?
    //public enum Slot{One, Two}

    //ctr
    protected Template(){}

    public Template(DayOfWeek weekday, LocalTime startTime){
        this.weekday = weekday;
        this.startTime = startTime;
        active = false;
    }

    public Template(DayOfWeek weekday, LocalTime startTime, Long id){
        this.weekday = weekday;
        this.startTime = startTime;
        this.id = id;
        active = false;
    }

    public Template(DayOfWeek weekday, LocalTime startTime, Long id, boolean active){
        this.weekday = weekday;
        this.startTime = startTime;
        this.id = id;
        this.active = active;
    }
    public Template(DayOfWeek weekday, LocalTime startTime, boolean active){
        this.weekday = weekday;
        this.startTime = startTime;
        this.active = active;
    }
    @Override
    public String toString(){
        return weekday.toString() + " " + startTime.toString() +
                " " + id + " active: " + active;
    }

//    @Override
//    public int compare(Template o1, Template o2) {
//        if (o1.getStartTime().isBefore(o2.getStartTime())){
//            return -1;
//        }else if (o1.getStartTime().equals(o2.getStartTime())){
//            return 0;
//        }else{
//            return 1;
//        }
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Template template = (Template) o;

        if (active != template.active) return false;
        if (id != template.id) return false;
        if (weekday != template.weekday) return false;
        return startTime != null ? startTime.equals(template.startTime) : template.startTime == null;
    }

    @Override
    public int hashCode() {
        int result = weekday != null ? weekday.hashCode() : 0;
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (active ? 1 : 0);
        result = (int) (31 * result + id);
        return result;
    }

    //GETTER, SETTER
    public DayOfWeek getWeekday() {
        return weekday;
    }

    public void setWeekday(DayOfWeek weekday) {
        this.weekday = weekday;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}