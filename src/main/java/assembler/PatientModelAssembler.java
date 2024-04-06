package assembler;

import controller.AppointmentController;
import controller.PatientController;
import controller.TemplateController;
import model.appointment.Appointment;
import model.appointment.Template;
import model.person.patient.Patient;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PatientModelAssembler implements RepresentationModelAssembler<Patient, EntityModel<Patient>> {


    @Override
    public EntityModel<Patient> toModel(Patient patient) {

        return EntityModel.of(patient,
                linkTo(methodOn(PatientController.class).one(patient.getId())).withSelfRel(),
                linkTo(methodOn(AppointmentController.class).all()).withRel("appointments"));
    }
}
