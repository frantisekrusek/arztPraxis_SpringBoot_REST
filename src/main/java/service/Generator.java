package service;

import model.Appointment;
import model.Template;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.Set;

public class Generator {

    private Office office;

    public Generator() {
    }

    /* Generates multiple Appointments from a single template.
    @param weeks determines number of weeks for appointment creation.
    e.g. 2 for a tuesdays-appointment = create 2 appointments: one for next tuesday, one for tuesday after that.
    Add an appointment, if it is for today and the start-time is yet to come */
    public Set generateAppsFromSingleTemplate_andRepeatByWeeks(Template template, int weeks, Instant dateOfGeneration) throws IllegalArgumentException{
        if (weeks<0) {throw new IllegalArgumentException();}
        //---------------------------------------------------------------------------------------------------
        Set<Appointment> appointments = new HashSet();
        TemporalAdjuster adjuster;
        int repetition = weeks;

        if (sameWeekdayAndTimeYetToCome(template, dateOfGeneration)){
            adjuster = TemporalAdjusters.nextOrSame(template.getWeekday());
            repetition += 1;
        }else{
            adjuster = TemporalAdjusters.next(template.getWeekday());
        }
        //---------------------------------------------------------------------------------------------------
        //Repeater:
        for (int i=0; i<repetition; i++){

            ZonedDateTime zdt = dateOfGeneration.atZone(office.getOffice_zoneId());
            zdt = zdt.
                    with(adjuster).
                    plus(Duration.ofDays(i*7)).
                    with(template.getStartTime());

            String name = zdt.format(DateTimeFormatter.ofPattern("HH:mm, EEEE dd.MM.uuuu"));

            Appointment appointment = new Appointment(name, zdt, false, null);
            appointments.add(appointment);
            System.out.println("LOG: appointment created:\nname: " + appointment.getName());
        }
        return appointments;
    }

    //Helper method
    public boolean sameWeekdayAndTimeYetToCome(Template t, Instant in){
        return t.getWeekday().equals(LocalDateTime.now().getDayOfWeek())
                && t.getStartTime().isAfter(LocalTime.ofInstant(in,this.office.getOffice_zoneId()));
    }

    //GETTER
    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }
}//end class


