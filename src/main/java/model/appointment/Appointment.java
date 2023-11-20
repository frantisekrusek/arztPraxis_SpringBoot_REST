package model.appointment;

import jakarta.persistence.*;
import model.person.patient.Patient;

import java.time.ZonedDateTime;

@Entity
public class Appointment implements Comparable {
    //name wird verwendet, um das Zeitfenster näher zu beschreiben.
    private String name;
    private ZonedDateTime dateTime;
    private boolean taken;
    @OneToOne
    private Patient patient;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    //ctr
    public Appointment(String name, ZonedDateTime dateTime, boolean taken) {
        this.name = name;
        this.dateTime = dateTime;
        this.taken = taken;
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
    public String toString(){
        return name + "\n";
    }
}//end class
