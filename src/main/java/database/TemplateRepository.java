package database;

import model.appointment.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.time.DayOfWeek;
import java.util.Set;

//nach https://spring.io/guides/gs/accessing-data-jpa/#initial
public interface TemplateRepository extends JpaRepository<Template, Long> {

    //todo: zwei identische Vorlagen vermeiden, wenn ein template erstellt wird

    Set<Template> findAllByWeekday(DayOfWeek weekday);


}
