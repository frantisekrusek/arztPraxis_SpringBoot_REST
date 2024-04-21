package assembler;

import controller.AppointmentController;
import model.Appointment;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AppointmentModelAssembler implements RepresentationModelAssembler<Appointment, EntityModel<Appointment>> {


    @Override
    public EntityModel<Appointment> toModel(Appointment appointment) {

        EntityModel<Appointment> appointmentModel = EntityModel.of(appointment,
                linkTo(methodOn(AppointmentController.class).one(appointment.getId())).withSelfRel(),
                linkTo(methodOn(AppointmentController.class).all()).withRel("appointments"));

        if (appointment.isTaken()) {
            appointmentModel.add(linkTo(methodOn(AppointmentController.class).
                    cancelAppointment(appointment.getId())).withRel("cancel"));

        }
        return appointmentModel;
    }
}
