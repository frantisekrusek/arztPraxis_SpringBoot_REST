package start;

import controller.Controller;
import database.*;
import exceptions.TemplateNotFoundException;
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
//@EnableJpaRepositories (basePackages={"database"})
@EntityScan(basePackages = "model")
//@EnableJpaRepositories (basePackageClasses=TemplateRepository.class)
@ComponentScan(basePackages = { "controller", "assembler", "start"})
public class Start {

    public static void main(String[] args) {

        SpringApplication.run(Start.class, args);

        //Controller controller = new Controller();
        //controller.selectUser();
    }

}
