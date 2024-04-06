package controller;

import assembler.AppointmentModelAssembler;
import assembler.PatientModelAssembler;
import database.AppointmentRepository;
import database.PatientRepository;
import exceptions.AppointmentNotFoundException;
import exceptions.PatientNotFoundException;
import exceptions.TemplateNotFoundException;
import model.appointment.Appointment;
import model.appointment.Template;
import model.person.patient.Patient;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class PatientController {
    private final PatientRepository repo;
    private final AppointmentRepository appointmentRepo;

    private final PatientModelAssembler assembler;

    public PatientController (PatientRepository repo, AppointmentRepository appointmentRepo, PatientModelAssembler assembler){
        this.repo = repo;
        this.appointmentRepo = appointmentRepo;
        this.assembler = assembler;
    }

    @GetMapping ("/patients/{id}")
    public EntityModel<Patient> one (@PathVariable Long id){

        Patient patient = repo.findById(id).
                orElseThrow(() -> new PatientNotFoundException(id));
        return assembler.toModel(patient);
    }

    @GetMapping ("/patients")
    public CollectionModel<EntityModel<Patient>> all(){

        List<EntityModel<Patient>> patients = repo.findAll().stream().
                map(assembler::toModel).collect(Collectors.toList());


        return CollectionModel.of(patients, linkTo(methodOn(PatientController.class).all()).withSelfRel());
    }

    @PostMapping ("/patients")
    public ResponseEntity<EntityModel<Patient>> newPatient(@RequestBody Patient patient){
        Patient newPatient = repo.save(patient);
        return ResponseEntity.
                created(linkTo(methodOn(PatientController.class).
                        one(newPatient.getId())).
                        toUri()).
                body(assembler.toModel(newPatient));
    }

    @DeleteMapping("patients/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable Long id){
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("patients/{id}")
    public ResponseEntity<?> replacePatient(@RequestBody Patient newPatient, @PathVariable Long id){

        Patient updatedPatient = repo.findById(id)
                .map(patient -> {
                    patient.setPhone_number(newPatient.getPhone_number());
                   // patient.setAppointments(newPatient.getAppointments());
                    return repo.save(patient);
                })
                .orElseGet(() -> {
                    newPatient.setId(id);
                    return repo.save(newPatient);
                });

        EntityModel<Patient> entityModel = assembler.toModel(updatedPatient);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    //@PutMapping ("patients/appointments/{patientid}, {appointmentid}")
    @PutMapping ("patients/{patientid}/appointments/{appointmentid}")
    public void assignAppointment(
            //@RequestBody Patient newPatient, @RequestBody Appointment newAppointment,
                                               @PathVariable Long patientid, @PathVariable Long appointmentid){

        System.out.println("LOG: METHOD assignAppointment");
        Appointment appointment = appointmentRepo.findById(appointmentid)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentid));
        Patient updatedPatient = repo.findById(patientid)
                .map((patient -> {
                    appointment.setPatient(patient);
                    return repo.save(patient);
                }))
                //todo


                .orElseThrow(() ->  new PatientNotFoundException(patientid));



        EntityModel<Patient> entityModel = assembler.toModel(updatedPatient);

        //return ResponseEntity
        //        .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
        //        .body(entityModel);
    }

}
