package start;

import controller.Controller;
import database.*;
import model.appointment.Appointment;
import model.appointment.Template;
import model.generator.Generator;
import model.generator.Supervisor;
import model.generator.updater.Clerk;
import model.office.Office;
import model.person.officeManager.OfficeManager;
import model.person.patient.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@SpringBootApplication
@EnableJpaRepositories (basePackages={"database"})
@EntityScan(basePackages = "model")
//@EnableJpaRepositories (basePackageClasses=TemplateRepository.class)
@ComponentScan(basePackages = { "controller", "assembler" })
public class Start {

    private static final Logger log = LoggerFactory.getLogger(Start.class);

    public static void main(String[] args) {

        SpringApplication.run(Start.class, args);

        //Controller controller = new Controller();
        //controller.selectUser();
    }//end main

    @Bean
    public CommandLineRunner simulate(TemplateRepository templateRepository,
                                      AppointmentRepository appointmentRepository,
                                      PatientRepository patientRepository,
                                      SupervisorRepository supervisorRepository){
        return (args) -> {

            //appointmentRepository.saveAll(Start.provideAppointments());
             supervisorRepository.save(Supervisor.getInstance());

//            // save a few template
//            templateRepository.save(new Template(DayOfWeek.SATURDAY, LocalTime.of(15,33)));
//            // fetch all templates
//            log.info("Templates found with findAll():");
//            log.info("-------------------------------");
//            for (Template template : templateRepository.findAll()) {
//                log.info(template.toString());
//            }
//            log.info("");
//            // fetch an template customer by ID
//            Template template = templateRepository.findById(1);
//            log.info("Template found with findById(1):");
//            log.info("--------------------------------");
//            log.info(template.toString());
//            log.info("");
//            // fetch templates by weekday
//            log.info("Templates found with findAllByWeekday(SATURDAY):");
//            log.info("--------------------------------------------");
//            templateRepository.findAllByWeekday(DayOfWeek.SATURDAY).forEach(sat_template -> {
//                log.info(sat_template.toString());
//            });
//            log.info("");
        };
    }//end simulate

    public static Set<Appointment> provideAppointments(){
        //String name = zdt.format(DateTimeFormatter.ofPattern("HH:mm, EEEE dd.MM.uuuu"));
        Appointment sunday_1300 = new Appointment(
                "13:00, Sunday 01.01.1995",
                ZonedDateTime.of(1995,1,1,13,0,0,0,
                        ZoneId.of("Europe/Vienna")),
                false);
        Appointment monday_0800 = new Appointment(
                "08:00, Monday 12.02.1934",
                ZonedDateTime.of(1934,2,12,8,0,0,0,
                        ZoneId.of("Europe/Vienna")),
                false);
        return new HashSet<Appointment>(Arrays.asList(sunday_1300, monday_0800));
    }//end provideAppointments
}
