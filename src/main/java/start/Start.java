package start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@EnableJpaRepositories (basePackages={"database"})
@EntityScan(basePackages = "model")
//@EnableJpaRepositories (basePackageClasses=TemplateRepository.class)
@ComponentScan(basePackages = { "controller", "assembler", "start", "service"})
public class Start {

    public static void main(String[] args) {

        SpringApplication.run(Start.class, args);

        System.out.println("LOG: Main Klasse");

    }

}
