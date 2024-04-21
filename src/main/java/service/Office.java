package service;

import model.Appointment;
import model.Template;
import model.Supervisor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
public class Office {
    //Each weekdays templates will be held in a Set, up to 7 Sets will be held in this array.
    private final Set<Template>[] templates;
    private final Set<Appointment> appointments;
    //TODO: fetchAppointments und insertAppointment in OfficeQueries
    private OfficeManager officeManager;
    private ZoneId office_zoneId = ZoneId.of("Europe/Vienna");
    private ZoneOffset offset = office_zoneId.getRules().getOffset(Instant.now());
    private Generator generator;
    private Timer timer;


    public Office(Clerk clerkP, OfficeManager officeManagerP) {

        this.generator = clerkP;
        Clerk clerk = (Clerk)this.generator;
        clerk.setWeeks(1);
        generator.setOffice(this);
        clerk.setOffice(this);
        officeManager = officeManagerP;
        officeManager.setOffice(this);

//        this.timer = new Timer();
//        LocalDateTime localDateTime = LocalDateTime.now().plusDays(1).with(LocalTime.MIN);
//        //LocalDateTime localDateTime = LocalDateTime.now();
//        Date nextTaskDate = Date.from(localDateTime.toInstant(offset));
//        timer.scheduleAtFixedRate(new Task((Clerk)this.generator), nextTaskDate, 1000L*60L*60L*24L);

        //timer.scheduleAtFixedRate(new Task(this), nextTaskDate, 5000L);
        //0..Sunday, 1 Monday, ... 6 Saturday
        //Why? To match Values of Enum java.time.DayOfWeek as close as possible.
        this.templates = new Set[]{
                new HashSet<Template>(), new HashSet<Template>(),
                new HashSet<Template>(), new HashSet<Template>(),
                new HashSet<Template>(), new HashSet<Template>(),
                new HashSet<Template>()};
        this.appointments = new HashSet<>();
    }//end Office(Clerk clerkP, OfficeManager officeManagerP)

    public Set<Template>[] mergeTemplates(Set<Template>[] newTemplates){
        for (int i=0; i<7; i++ ){
            this.templates[i].addAll(newTemplates[i]);
        }
        return templates;
    }//end mergeTemplates()


    public Set<Template>[] fromListTo_ArrayOfSets(List<Template> newTemplates){

        Set<Template>[] templateSetArray = new Set[]{
                new HashSet<Template>(), new HashSet<Template>(),
                new HashSet<Template>(), new HashSet<Template>(),
                new HashSet<Template>(), new HashSet<Template>(),
                new HashSet<Template>()};

        for(Template t : newTemplates){
            int value = t.getWeekday().getValue();
            if(value == 7){
                value = 0;
            }
            templateSetArray[value].add(t);
        }

        return templateSetArray;
    }


    public List<Template> fromArrayOfSets_ToList(Set<Template>[] templateSetArray){
        List<Template> templateList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            templateList.addAll(templateSetArray[i]);
        }

        return templateList;
    }


    public Set<Template>[] printTemplates(Set<Template>[] templates){

        for (int i=0; i<7; i++ ){
            System.out.println("LOG: " + templates[i]);
        }
        return templates;
    }

    public void printAppointments(){
        for (Appointment a: this.appointments){
            System.out.println("");
            System.out.println(a.toString());
        }
    }


    public ZonedDateTime lastUpdateToZDT(){
        return ZonedDateTime.ofInstant(Supervisor.getInstance().getLastUpdate(), office_zoneId);
    }

    //GETTER, SETTER
    public Set[] getTemplates() {
        return templates;
    }

    public Set<Appointment> getAppointments() {
        return appointments;
    }

    public ZoneId getOffice_zoneId() {
        return office_zoneId;
    }

    public void setOffice_zoneId(ZoneId office_zoneId) {
        this.office_zoneId = office_zoneId;
    }

    public ZoneOffset getOffset() {
        return offset;
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;
    }

    public Generator getGenerator() {
        return generator;
    }
}//end class
