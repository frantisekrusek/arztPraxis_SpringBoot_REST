package database;

import model.appointment.Appointment;

public interface CustomAppointmentRepository {

    public Appointment customFindById(Long id);


}