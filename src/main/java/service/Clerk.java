package service;

import model.Appointment;
import model.Template;
import model.Supervisor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import start.BusinessLogicConfig;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
/*Clerk (Updater ?) is responsible for creating the correct amount of future appointments. */
public class Clerk extends Generator {

    private static final Logger log = LoggerFactory.getLogger(BusinessLogicConfig.class);
    private int weeks;
    private LocalTime lastTemplateOfDay = LocalTime.MIDNIGHT;

    @Autowired
    private JpaRepository<Template, Long> templateRepo;
    @Autowired
    private JpaRepository<Supervisor, Long> supervisorRepo;
    @Autowired
    private JpaRepository<Appointment, Long> appointmentRepo;


    //ctr
    public Clerk() {
    }

    /* "Startmethode"
    can be used to initialize whole schedule.
    templatesFromRepo has 7 Sets of templates, one for each weekday.
     */
    public void initSchedule(JpaRepository<Template, Long> repo) {
        log.info("initSchedule()");

        Set<Template>[] templatesFromRepo = new Set[]{
                new HashSet<Template>(), new HashSet<Template>(),
                new HashSet<Template>(), new HashSet<Template>(),
                new HashSet<Template>(), new HashSet<Template>(),
                new HashSet<Template>()};
        for (Template t : repo.findAll()){
            int day = t.getWeekday().getValue() - 1;
            templatesFromRepo[day].add(t);

        }

        for (Set<Template> templateSet : templatesFromRepo) {
            initSchedule(templateSet);
        }

    }

    /* Hilfsmethode
    to be used as helper-method to initialize whole schedule or to
    activate a group of templates.
    e.g. generate appointments for all Mondays of the next ... weeks.
    Algo:
    Iterates through templates.
    Checks if templates are active.
    Creates f(weeks) number of appointments from each template.
    Writes appointments into repository. todo
     */
    public void initSchedule(Set<Template> templates) {
        Set<Appointment> appointments = super.getOffice().getAppointments();
        for (Template template : templates) {
            if (template.isActive()) {
                appointments.addAll(super.generateAppsFromSingleTemplate_andRepeatByWeeks(template, this.weeks, Instant.now()));
                //repo
            }
        }
    }


    /*
     ?? Aufruf der Methode täglich um 00:00h (LocalTime.MIN)
     Instant.now() muss Parameter sein, um Methode testbar zu machen.
     */
    public Set<Appointment> catchUp(Instant now, Set<Template>[] templateArr) {

        Set<Appointment> setOfApps = new LinkedHashSet<>();
        Instant lastUpdate = supervisorRepo.findById(1L).get().getLastUpdate();
        log.info("lastUpdate: " + lastUpdate.toString());
        log.info("now(): " + now.toString());
        boolean lastUpdate_is_some_time_and_date_before_today_midnight =
                now.minus(24, ChronoUnit.HOURS).isAfter(lastUpdate);
        log.info("Class Clerk: now.minus 24h isAfter lastUpdate? "
                + lastUpdate_is_some_time_and_date_before_today_midnight);
        while (lastUpdate_is_some_time_and_date_before_today_midnight) {
            //CORE
            Instant newLastUpdate = supervisorRepo.findById(1L).get().getLastUpdate();
            setOfApps.addAll(generateAppsOfDay(newLastUpdate, templateArr));
            log.info("newLastUpdate: " + LocalDateTime.ofInstant(
                    newLastUpdate = supervisorRepo.findById(1L).get().getLastUpdate(), ZoneOffset.systemDefault()));
            lastUpdate_is_some_time_and_date_before_today_midnight =
                    now.minus(24, ChronoUnit.HOURS).isAfter(newLastUpdate);
            log.info(String.valueOf(lastUpdate_is_some_time_and_date_before_today_midnight));

        }
        return setOfApps;
    }
    //END catchUp


    /* "24h Method":
    creates one appointment per template for the following date:
     'lastUpdate' + 1 day + weeks x 7 days (depending on @param weeks, e.g. 1+2x7 ).
     @param templateArray should be Set<Template>[] templates from office.*/
    public Set<Appointment> generateAppsOfDay(Instant lastUpdate, Set<Template>[] templateArray) {
        log.info("generateAppsOfDay");
        Set<Appointment> appointments = new LinkedHashSet<>();
        //System.out.println("LOG Clerk lastupdate: " + LocalDateTime.ofInstant(lastUpdate, getOffice().getOffice_zoneId()));
        ZonedDateTime zonedLastUpdate = ZonedDateTime.ofInstant(lastUpdate, super.getOffice().getOffice_zoneId());

        int weekday = this.findWeekdayFollowingLastUpdate(zonedLastUpdate);

        for (Template template : templateArray[weekday]) {
            if (template.isActive()) {
                LocalDate dateOfApp = zonedLastUpdate.toLocalDate().plusDays(1 + (weeks * 7));
                LocalTime timeOfApp = template.getStartTime();
                ZonedDateTime datetimeOfApp = ZonedDateTime.of(dateOfApp, timeOfApp, super.getOffice().getOffice_zoneId());
                String name = datetimeOfApp.format(DateTimeFormatter.ofPattern("HH:mm, EEEE dd.MM.uuuu"));
                appointmentRepo.save(new Appointment(name, datetimeOfApp, false, null));
                //appointments.add(new Appointment(name, datetimeOfApp, false, null));
                log.info("appointment created: " + name);
            }
        }
        this.archiveAppointments();
        this.moveCursorOfLastUpdatedTemplate(zonedLastUpdate);

        return appointments;
    }//end generateAppsOfDay()


    //Helper method
    //entfernen?
    public LocalTime findLastTemplate(LocalTime timeOfApp) {

        if (timeOfApp.isAfter(lastTemplateOfDay)) {
            lastTemplateOfDay = timeOfApp;
        }
        return lastTemplateOfDay;
    }

    //Helper method
    //change lastUpdate to (lastUpdatedTemplate + 1 day).
    public void moveCursorOfLastUpdatedTemplate(ZonedDateTime zonedLastUpdate) {
        ZonedDateTime newZDT = zonedLastUpdate.plus(1, ChronoUnit.DAYS).
                with(LocalTime.MIN);
        log.info("new lastUpdate will be set to : " + newZDT);
        Supervisor updatedSupervisor = Supervisor.getInstance();
        updatedSupervisor.setLastUpdate(newZDT.toInstant());
        supervisorRepo.save(updatedSupervisor);
    }

    //Helper method
    public int findWeekdayFollowingLastUpdate(ZonedDateTime zonedLastUpdate) {
        int weekday;
        weekday = zonedLastUpdate.plusDays(1).getDayOfWeek().getValue();
        if (weekday == 7) {
            weekday = 0;
        }
        return weekday;
    }


    public ArrayList<Appointment> sortAppointments() {
        Set<Appointment> appointments = this.getOffice().getAppointments();
        ArrayList arrayList = new ArrayList<>(appointments);
        Collections.sort(arrayList);
        return arrayList;
    }

    public void archiveAppointments (){
        List<Appointment> appointmentList = appointmentRepo.findAll().stream().map((a -> {
            if(a.getDateTime().isBefore(ZonedDateTime.now())){
                a.setArchived(true);
            }
            return a;
        })).collect(Collectors.toList());
        appointmentRepo.saveAll(appointmentList);
    }


    //GETTER, SETTER
    public int getWeeks() {
        return weeks;
    }

    public void setWeeks(int weeks) {
        this.weeks = weeks;
    }

    public LocalTime getLastTemplateOfDay() {
        return lastTemplateOfDay;
    }


    //public Office getOffice() {        return office;    }

}//end class
