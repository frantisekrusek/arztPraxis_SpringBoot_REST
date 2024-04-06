package database;

import model.appointment.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long>, CustomAppointmentRepository {

    public List<Appointment> findByTaken(boolean taken);

    public List<Appointment> findByPatientId(Long patientId);

    public List<Appointment> findByNameContaining(String name);
}
