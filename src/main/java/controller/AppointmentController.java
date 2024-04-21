package controller;

import assembler.AppointmentModelAssembler;
import database.AppointmentRepository;
import database.PatientRepository;
import exceptions.AppointmentNotFoundException;
import exceptions.PatientNotFoundException;
import model.Appointment;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class AppointmentController {
    private final AppointmentRepository appointmentRepository;
    private final AppointmentModelAssembler assembler;
    private final PatientRepository patientRepository;

    public AppointmentController (AppointmentRepository appointmentRepository, AppointmentModelAssembler assembler,
                                  PatientRepository patientRepository){
        this.appointmentRepository = appointmentRepository;
        this.assembler = assembler;
        this.patientRepository = patientRepository;
    }

    @GetMapping ("/appointments/{id}")
    public EntityModel<Appointment> one (@PathVariable Long id){

        Appointment appointment = appointmentRepository.findById(id).
                orElseThrow(() -> new AppointmentNotFoundException(id));
        return assembler.toModel(appointment);
    }

    @GetMapping ("/appointments")
    public CollectionModel<EntityModel<Appointment>> all(){

        List<EntityModel<Appointment>> appointments = appointmentRepository.findAll().stream().
                map(assembler::toModel).collect(Collectors.toList());


        return CollectionModel.of(appointments, linkTo(methodOn(AppointmentController.class).all()).withSelfRel());
    }

    @GetMapping("/patients/{patientId}/appointments")
    public CollectionModel<EntityModel<Appointment>> appointmentsByPatientId(@PathVariable Long patientId){

        if(!patientRepository.existsById(patientId)){
            throw new PatientNotFoundException(patientId);
        }

        List<EntityModel<Appointment>> appointments = appointmentRepository.findByPatientId(patientId).stream().
                map(assembler::toModel).collect(Collectors.toList());

        return CollectionModel.of(appointments, linkTo(methodOn(AppointmentController.class).
                appointmentsByPatientId(patientId)).withSelfRel());
    }

    @PostMapping ("/appointments")
    public ResponseEntity<EntityModel<Appointment>> newAppointment(@RequestBody Appointment appointment){
        Appointment newAppointment = appointmentRepository.save(appointment);
        return ResponseEntity.
                created(linkTo(methodOn(AppointmentController.class).
                        one(newAppointment.getId())).
                        toUri()).
                body(assembler.toModel(newAppointment));
    }

    @DeleteMapping("appointments/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id){
        appointmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("appointments/{id}")
    public ResponseEntity<?> replaceAppointment(@RequestBody Appointment newAppointment, @PathVariable Long id){

        Appointment updatedAppointment = appointmentRepository.findById(id)
                .map(appointment -> {
                    appointment.setName(newAppointment.getName());
                    appointment.setDateTime(newAppointment.getDateTime());
                    appointment.setTaken(newAppointment.isTaken());
                    appointment.setPatient(newAppointment.getPatient());
                    return appointmentRepository.save(appointment);
                })
                .orElseGet(() -> {
                    newAppointment.setId(id);
                    return appointmentRepository.save(newAppointment);
                });

        EntityModel<Appointment> entityModel = assembler.toModel(updatedAppointment);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("appointments/{appointmentId}/patient/{patientId}")
    public ResponseEntity<?> assignAppointment(@PathVariable Long patientId,
                                               @PathVariable Long appointmentId){
        if(!patientRepository.existsById(patientId)){
            throw new PatientNotFoundException(patientId);
        }
                Appointment updatedAppointment = appointmentRepository.findById(appointmentId)
                .map(appointment -> {
                    appointment.setPatient(patientRepository.findById(patientId).
                            orElseThrow(() -> new PatientNotFoundException(patientId)));
                    appointment.setTaken(true);
                    return appointmentRepository.save(appointment);
                }).orElseThrow();

        EntityModel<Appointment> entityModel = assembler.toModel(updatedAppointment);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/appointments/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id) {

        Appointment appointment = appointmentRepository.findById(id) //
                .orElseThrow(() -> new AppointmentNotFoundException(id));

        if (appointment.isTaken()) {
            appointment.setPatient(null);
            appointment.setTaken(false);

            return ResponseEntity.ok(assembler.toModel(appointmentRepository.save(appointment)));
        }

        return ResponseEntity //
                .status(HttpStatus.METHOD_NOT_ALLOWED) //
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE) //
                .body(Problem.create() //
                        .withTitle("Method not allowed") //
                        .withDetail("You can't cancel an appointment that is not assigned to a patient"));
    }


}
