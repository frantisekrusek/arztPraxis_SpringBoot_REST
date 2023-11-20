package model.person.officeManager;

import database.TemplateRepository;
import model.appointment.Template;
import model.office.Office;
import model.person.Person;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@RestController
public class OfficeManager extends Person {

    TemplateRepository templateRepository;
    private Office office;

    //ctr
    public OfficeManager() {
    }

    /*
    OfficeManager creates Templates from given Weekday and Starttime.
    New Template is written into database.
     */
    public Template createTemplate(DayOfWeek dayOfWeek, LocalTime localTime){
        Template template = new Template(dayOfWeek, localTime);
        if (categorizeTemplate(template)){
            System.out.println("LOG: template created: "
                    + template.getWeekday().toString() + " " + template.getStartTime().toString());
            update(template);
        }else {
            System.out.println("LOG: Template already exists!");
        }
        return template;
    }

    public void update(Template template){
        templateRepository.save(template);
        }

    /*
    Method prevents creating duplicate.
    Templates are categorized by Weekday - preparation for automated day-by-day appointment creation.
     */
    public boolean categorizeTemplate(Template template){
        Set<Template>[] templates = this.office.getTemplates();
        //1=Monday..7=Sunday
        int i = template.getWeekday().getValue();
        boolean added = false;
        switch (i){
            case 1: added = templates[1].add(template); break;
            case 2: added = templates[2].add(template); break;
            case 3: added = templates[3].add(template); break;
            case 4: added = templates[4].add(template); break;
            case 5: added = templates[5].add(template); break;
            case 6: added = templates[6].add(template); break;
            //DayOfWeek int value 7 -> array index 0
            case 7: added = templates[0].add(template); break;
        }
        return added;
    }

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }
}
