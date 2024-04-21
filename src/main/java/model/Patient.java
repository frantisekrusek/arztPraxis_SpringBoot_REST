package model;

import jakarta.persistence.*;
import model.Person;

@Entity
public class Patient extends Person {
    private String phone_number;

//    @OneToMany(mappedBy = "patient")
//    private Set<Appointment> appointments;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //ctr
    public Patient(String pPhone_number, String pFirst_name, String pSurname){
        this.phone_number =pPhone_number;
        this.setFirst_name(pFirst_name);
        this.setSurname(pSurname);
    }//end ctr

    public Patient() {

    }

    /*
    Patient is linked to Appointment and contrariwise.
     */
//    public Appointment makeAppointment(Appointment pAppointment){
//        pAppointment.setPatient(this);
//        this.appointments.add(pAppointment);
//        pAppointment.setTaken(true);
//        System.out.println("LOG: " + this.toString() + " has an appointment for " + pAppointment);
//
//        return pAppointment;
//    }//end makeAppointment()

    @Override
    public String toString() {
        return this.getFirst_name() + " " + this.getSurname() + " (Tel " + phone_number + ")";
    }


//    public void addAppointment (Appointment appointment){
//        this.appointments.add(appointment);
//    }
    //GETTER, SETTER

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

//    public Set<Appointment> getAppointments() {
//        return appointments;
//    }

    public Long getId() {
        return id;
    }

//    public void setAppointments(Set<Appointment> appointments) {
//        this.appointments = appointments;
//    }

    public void setId(Long id) {
        this.id = id;
    }

}
