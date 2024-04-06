package assembler;

import controller.AppointmentController;
import controller.TemplateController;
import model.appointment.Appointment;
import model.appointment.Template;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AppointmentModelAssembler implements RepresentationModelAssembler<Appointment, EntityModel<Appointment>> {


    @Override
    public EntityModel<Appointment> toModel(Appointment appointment) {

        return EntityModel.of(appointment,
                linkTo(methodOn(AppointmentController.class).one(appointment.getId())).withSelfRel(),
                linkTo(methodOn(AppointmentController.class).all()).withRel("appointments"));
    }
}
