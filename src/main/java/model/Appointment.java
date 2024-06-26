package model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@NamedEntityGraph(name = "graph.Appointment.patient",
        attributeNodes = @NamedAttributeNode("patient"))
public class Appointment implements Comparable, Serializable {
    //name wird verwendet, um das Zeitfenster näher zu beschreiben.
    private String name;
    private ZonedDateTime dateTime;
    private boolean taken;

    private boolean archived;

    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name="patient_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Patient patient;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //ctr
    public Appointment(String name, ZonedDateTime dateTime, boolean taken, Patient patient) {
        this.name = name;
        this.dateTime = dateTime;
        this.taken = taken;
        this.patient = patient;
    }

    public Appointment() {

    }

    //GETTER, SETTER
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }
    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isTaken() {
        return taken;
    }
    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {this.id = id;}

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    @Override
    public int compareTo(Object o) {
        Appointment appointment = (Appointment)o;
        if (appointment.dateTime.isBefore(this.dateTime)){
            return 1;
        }else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Appointment that = (Appointment) o;

        if (taken != that.taken) return false;
        if (!name.equals(that.name)) return false;
        return dateTime.equals(that.dateTime);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + dateTime.hashCode();
        result = 31 * result + (taken ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "name='" + name + '\'' +
                ", dateTime=" + dateTime +
                ", taken=" + taken +
                ", patient=" + patient +
                ", id=" + id +
                '}';
    }
}//end class
