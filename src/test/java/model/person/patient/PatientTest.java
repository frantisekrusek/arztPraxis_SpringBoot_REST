package model.person.patient;

import static org.junit.jupiter.api.Assertions.*;

import model.appointment.Appointment;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class PatientTest {

    @Test
    void testMakeAppointment() {
        Patient patient = new Patient("0123", "firstname", "surname");
        ZonedDateTime datetime = ZonedDateTime.now();
        String name = datetime.format(DateTimeFormatter.ofPattern("hh:mm, EEEE dd.MM.uuuu"));
        Appointment appointment = new Appointment(name, datetime, false);
        patient.makeAppointment(appointment);

        //hat p a?
        assertNotNull(patient.getAppointment(), "patient has no appointment");
        //hat a p?
        assertNotNull(appointment.getPatient(), "appointment has no assignment");
        //ist a belegt?
        assertTrue(appointment.isTaken(), "appointment is assigned but not marked as taken");
    }
}
