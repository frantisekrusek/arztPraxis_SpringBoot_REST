package start;

import database.AppointmentRepository;
import database.PatientRepository;
import database.SupervisorRepository;
import database.TemplateRepository;
import exceptions.SupervisorNotFoundException;
import exceptions.TemplateNotFoundException;
import model.Appointment;
import model.Template;
import model.Supervisor;
import org.springframework.beans.factory.annotation.Autowired;
import service.Office;
import service.Clerk;
import model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import service.Task;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;

@Configuration
@EntityScan(basePackages = "model")
@EnableJpaRepositories(basePackages={"database"})
public class BusinessLogicConfig {

    private static final Logger log = LoggerFactory.getLogger(BusinessLogicConfig.class);

    @Autowired
    SupervisorRepository supervisorRepository;

    private Timer timer = new Timer();
    @Bean
    @Order(1)
    public CommandLineRunner initDatabase(TemplateRepository templateRepository,
                                          AppointmentRepository appointmentRepository,
                                          PatientRepository patientRepository,
                                          SupervisorRepository supervisorRepository){

        return (args) -> {
            log.info("ORDER 1: initDatabase");

            supervisorRepository.save(Supervisor.getInstance());
            log.info("");
            log.info(supervisorRepository.findById(1L).orElseThrow(() -> new SupervisorNotFoundException(1L)).
                    getLastUpdate().toString());

            // save a few templates
            ArrayList<Template> list = new ArrayList<>();
            list.add(new Template(DayOfWeek.MONDAY, LocalTime.of(9, 0), true));
            list.add(new Template(DayOfWeek.MONDAY, LocalTime.of(10, 0), true));
            list.add(new Template(DayOfWeek.TUESDAY, LocalTime.of(9, 0), true));
            list.add(new Template(DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), true));
            list.add(new Template(DayOfWeek.THURSDAY, LocalTime.of(9, 0), true));
            list.add(new Template(DayOfWeek.FRIDAY, LocalTime.of(9, 0), true));
            list.add(new Template(DayOfWeek.SATURDAY, LocalTime.of(15, 30), true));
            list.add(new Template(DayOfWeek.SUNDAY, LocalTime.of(15, 30), true));
            templateRepository.saveAll(list);
            // fetch all templates
            log.info("Templates found with findAll():");
            log.info("-------------------------------");
            for (Template template : templateRepository.findAll()) {
                log.info(template.toString());
            }
            log.info("");
            // fetch a template by ID
            Template template = templateRepository.findById(1L).orElseThrow(() -> new TemplateNotFoundException(1L));
            log.info("Template found with findById(1):");
            log.info("--------------------------------");
            log.info(template.toString());
            log.info("");
            // fetch templates by weekday
            log.info("Templates found with findAllByWeekday(SATURDAY):");
            log.info("--------------------------------------------");
            templateRepository.findAllByWeekday(DayOfWeek.SATURDAY).forEach(sat_template -> {
                log.info(sat_template.toString());
            });
            log.info("");

            // save patients
            Patient julius = new Patient("0676 50 60 70 80", "Julius",
                    "Cesar");
            patientRepository.save(julius);
            Patient joe = new Patient("0999 7777777", "Joe",
                    "Dalton");
            patientRepository.save(joe);
            // fetch all patients
            log.info("Patients found with findAll():");
            log.info("-------------------------------");
            for (Patient patient : patientRepository.findAll()) {
                log.info(patient.toString());
            }
            log.info("");

            // save appointments
            appointmentRepository.save(new Appointment(
                    "13:00, Sunday 01.01.1995",
                    ZonedDateTime.of(1995,1,1,13,0,0,0,
                            ZoneId.of("Europe/Vienna")),
                    false, null));
            appointmentRepository.save(new Appointment(
                    "08:00, Monday 12.02.1934",
                    ZonedDateTime.of(1934,2,12,8,0,0,0,
                            ZoneId.of("Europe/Vienna")),
                    true, julius));
            // fetch all appointments
            log.info("Appointments found with findAll():");
            log.info("-------------------------------");
            for (Appointment appointment : appointmentRepository.findAll()) {
                log.info(appointment.toString());
                if (appointment.getPatient() != null){
                    log.info(appointment.getPatient().toString());

                }
            }
            log.info("");

            Appointment appointment = appointmentRepository.customFindById(2L);
            log.info(appointment.toString());

        };
    }

    @Bean
    @Order (2)
    public CommandLineRunner startBusinessLogic(TemplateRepository templateRepository,
                                                SupervisorRepository supervisorRepository,
                                                Clerk clerk,
                                                Office office){
        return (args) -> {
            log.info("ORDER 2: businessLogic");

            //run to enable scenario B:
            Supervisor mockSupervisor = Supervisor.getInstance();
            mockSupervisor.setScheduleInitialized(true);
            mockSupervisor.setId(1L);
            supervisorRepository.save(mockSupervisor);
            //

            /* A) Initialize the schedule:
            ****************************
            The assumption here is that the application runs for the very first time.
            The code checks if the schedule has been initialized yet. If it has not,
            appointments are created from existing templates for a certain amounts of
            weeks from today on. The amount of weeks can be customized.
            */
            log.info("LastUpdate: "
                    + supervisorRepository.findById(1L).orElseThrow().getLastUpdate().toString() );
            log.info("isScheduleInitialized: "
                    + supervisorRepository.findById(1L).orElseThrow().isScheduleInitialized());

            //CONDITION:
            if(!supervisorRepository.findById(1L).orElseThrow().isScheduleInitialized()){
                clerk.setWeeks(2);
                //CORE:
                clerk.initSchedule(templateRepository);

                Supervisor updatedSupervisor = supervisorRepository.findById(1L).orElseThrow();
                updatedSupervisor.setScheduleInitialized(true);
                supervisorRepository.save(updatedSupervisor);
            }


//            log.info("isScheduleInitialized: "
//                    + supervisorRepository.findById(1L).orElseThrow().isScheduleInitialized());

//            log.info("Appointments");
//            office.printAppointments();

//            log.info("LastUpdate: " + supervisorRepository.findById(1L).orElseThrow().getLastUpdate().toString() );
//
//            if(!supervisorRepository.findById(1L).orElseThrow().isScheduleInitialized()) {
//                log.info("Schedule is not initialized");
//                clerk.setWeeks(2);
//                clerk.initSchedule(templateRepository);
//
//            }else{
//                log.info("Schedule is already initialized!");
//            }


            /* B) Catch up with the schedule:
            *******************************

             */

            Supervisor mockSupervisor_2 = Supervisor.getInstance();
            mockSupervisor_2.setLastUpdate(Instant.now().minus(10, ChronoUnit.DAYS));
            mockSupervisor_2.setId(1L);
            supervisorRepository.save(mockSupervisor_2);
            log.info("mocked lastUpdate: " + String.valueOf(supervisorRepository.findById(1L).get().getLastUpdate()));
            //CORE
            clerk.catchUp(Instant.now(),
                    office.fromListTo_ArrayOfSets(templateRepository.findAll()
            ));

            //todo


            /* C) Set a Timer with a task.
            //****************************
            Run the task at the beginning of each calendar day.
            The task is to
            -fetch all Templates from the repository,
            -create appointments for 'new day + x weeks'
            -set Supervisor.lastUpdate to the new calendar day, 00:00h.
            For the purpose of demonstration, code is provided that will run this task
            every 5 seconds.

            Example:
            It is Monday, January 1st, 23:00h. lastUpdate is set to 01-01, 00:00h.
            @param Clerk.weeks = 2.
            One hour later, at midnight, all appointments for Tuesday, January 16th are created
            and lastUpdate ist set to 01-02, 00:00h.
                (Appointments from 1st until 16th have already been created either through
            initializing the schedule or through this very same task, that was run every day
            at midnight since December 18th.)
             */

            ZoneId office_zoneId = ZoneId.of("Europe/Vienna");
            ZoneOffset offset = office_zoneId.getRules().getOffset(Instant.now());

            /*for production:
            * */
            LocalDateTime localDateTime = LocalDateTime.now().plusDays(1).with(LocalTime.MIN);
            Date nextTaskDate = Date.from(localDateTime.toInstant(offset));
            timer.scheduleAtFixedRate(new Task(clerk, office, supervisorRepository, templateRepository), nextTaskDate,
                     1000L*60L*60L*24L);

            /*for demonstration:
            * */
//            LocalDateTime localDateTime = LocalDateTime.now();
//            Date nextTaskDate = Date.from(localDateTime.toInstant(offset));
//            timer.scheduleAtFixedRate(new Task(clerk, office, supervisorRepository, templateRepository), nextTaskDate, 5000L);

        };

    }

}
