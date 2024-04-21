package database;

import model.Appointment;

public interface CustomAppointmentRepository {

    public Appointment customFindById(Long id);


}