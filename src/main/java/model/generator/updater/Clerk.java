package model.generator.updater;

import model.appointment.Appointment;
import model.appointment.Template;
import model.generator.Generator;
import model.generator.Supervisor;
import model.office.Office;
import model.office.Task;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/*Clerk (Updater ?) is responsible for creating the correct amount of future appointments. */
public class Clerk extends Generator {

    private int weeks;
    private LocalTime lastTemplateOfDay = LocalTime.MIDNIGHT;


    //ctr
    public Clerk() {
    }

    /* "Startmethode"
    can be used to initialize whole schedule.
    @param templatesArr most likely has 7 Sets of templates, one for each weekday.
     */
    public void unleashTemplates(Set<Template>[] templatesArr) {
        for (Set<Template> templateSet : templatesArr) {
            unleashTemplates(templateSet);
        }
    }//end unleashTemplates()

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
    public void unleashTemplates(Set<Template> templates) {
        Set<Appointment> appointments = super.getOffice().getAppointments();
        for (Template template : templates) {
            if (template.isActive()) {
                appointments.addAll(super.generateAppsFromSingleTemplate(template, this.weeks, Instant.now()));
                //repo
            }
        }
    }//end unleashTemplates()


    /* "24h Method":
    creates one appointment per template for the following date:
     'lastUpdate' + 1 day + weeks x 7 days (depending on @param weeks, e.g. 1+2x7 ).
     @param templateArray should be Set<Template>[] templates from office.*/
    public Set<Appointment> generateAppsOfDay(Instant lastUpdate, Set<Template>[] templateArray) {
        Set<Appointment> appointments = new LinkedHashSet<>(); //because it is ordered
        //System.out.println("LOG Clerk 74 lastupdate: " + LocalDateTime.ofInstant(lastUpdate, getOffice().getOffice_zoneId()));
        ZonedDateTime zonedLastUpdate = ZonedDateTime.ofInstant(lastUpdate, super.getOffice().getOffice_zoneId());

        int weekday = this.findWeekdayFollowingLastUpdate(zonedLastUpdate);

        for (Template template : templateArray[weekday]) {
            if (template.isActive()) {
                LocalDate dateOfApp = zonedLastUpdate.toLocalDate().plusDays(1 + (weeks * 7));
                LocalTime timeOfApp = template.getStartTime();
                ZonedDateTime datetimeOfApp = ZonedDateTime.of(dateOfApp, timeOfApp, super.getOffice().getOffice_zoneId());
                String name = datetimeOfApp.format(DateTimeFormatter.ofPattern("HH:mm, EEEE dd.MM.uuuu"));
                appointments.add(new Appointment(name, datetimeOfApp, false));
                System.out.println("LOG: appointment created:\nname: " + name);
            }
        }

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
        Supervisor.getInstance().setLastUpdate(newZDT.toInstant());
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

    /*
     Aufruf der Methode täglich um 00:00h (LocalTime.MIN)
     Instant.now() muss Parameter sein, um Methode testbar zu machen.
     */
    public Set<Appointment> catchUp(Instant now, Set<Template>[] templateArr) {

        Set<Appointment> setOfApps = new LinkedHashSet<>();
        System.out.println("LOG: " + now.minus(24, ChronoUnit.HOURS).isAfter(Supervisor.getInstance().getLastUpdate()));
        while (now.minus(24, ChronoUnit.HOURS).isAfter(Supervisor.getInstance().getLastUpdate())) {
            //CORE
            setOfApps.addAll(generateAppsOfDay(Supervisor.getInstance().getLastUpdate(), templateArr));
        }
        return setOfApps;
    }
    //END catchUp

    public ArrayList<Appointment> sortAppointments() {
        Set<Appointment> appointments = this.getOffice().getAppointments();
        ArrayList arrayList = new ArrayList<>(appointments);
        Collections.sort(arrayList);
        return arrayList;
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
