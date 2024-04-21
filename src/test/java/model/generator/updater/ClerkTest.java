package model.generator.updater;

import model.Appointment;
import model.Template;
import service.Generator;
import model.Supervisor;
import service.Office;
import service.OfficeManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import service.Clerk;


import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ClerkTest {

    private Generator mockGenerator;
    private Clerk mockClerk;
    private ZoneId myZoneId;
    private Template activeTemplate_Mon_00_00, activeTemplate_Mon_08_00, activeTemplate_Mon_08_15,
            activeTemplate_Tue_11_00, activeTemplate_Tue_12_00,
            activeTemplate_Wed_11_00,
            activeTemplate_Thu_11_00,
            activeTemplate_Fri_11_00,
            activeTemplate_Sat_11_00,
            activeTemplate_Sun_07_00;
    private Template inactiveTemplate;
    private Set<Template>[] mockTemplatesArr;

    @BeforeEach
    public void setUpMocks(){

        mockGenerator = new Clerk();
        mockClerk = (Clerk)mockGenerator;
        Office office = new Office((Clerk)mockGenerator,new OfficeManager());
        mockGenerator.setOffice(office);
        office.setGenerator(mockClerk);
        myZoneId = mockClerk.getOffice().getOffice_zoneId();
        activeTemplate_Mon_00_00 = new Template(DayOfWeek.MONDAY, LocalTime.MIDNIGHT); activeTemplate_Mon_00_00.setActive(true);
        activeTemplate_Mon_08_00 = new Template(DayOfWeek.MONDAY, LocalTime.of(8,00)); activeTemplate_Mon_08_00.setActive(true);
        activeTemplate_Mon_08_15 = new Template(DayOfWeek.MONDAY, LocalTime.of(8,15)); activeTemplate_Mon_08_15.setActive(true);
        activeTemplate_Tue_11_00 = new Template(DayOfWeek.TUESDAY, LocalTime.of(11,00)); activeTemplate_Tue_11_00.setActive(true);
        activeTemplate_Tue_12_00 = new Template(DayOfWeek.TUESDAY, LocalTime.of(12,00)); activeTemplate_Tue_12_00.setActive(true);
        activeTemplate_Wed_11_00 = new Template(DayOfWeek.WEDNESDAY, LocalTime.of(11,00)); activeTemplate_Wed_11_00.setActive(true);
        activeTemplate_Thu_11_00 = new Template(DayOfWeek.THURSDAY, LocalTime.of(11,00)); activeTemplate_Thu_11_00.setActive(true);
        activeTemplate_Fri_11_00 = new Template(DayOfWeek.FRIDAY, LocalTime.of(11,00)); activeTemplate_Fri_11_00.setActive(true);
        activeTemplate_Sat_11_00 = new Template(DayOfWeek.SATURDAY, LocalTime.of(11,00)); activeTemplate_Sat_11_00.setActive(true);
        activeTemplate_Sun_07_00 = new Template(DayOfWeek.SUNDAY, LocalTime.of(7,00)); activeTemplate_Sun_07_00.setActive(true);
        inactiveTemplate = new Template(DayOfWeek.WEDNESDAY, LocalTime.NOON);
        mockTemplatesArr = new Set[]{
                new HashSet<Template>(Arrays.asList(activeTemplate_Sun_07_00)),
                new HashSet<Template>(Arrays.asList(activeTemplate_Mon_00_00, activeTemplate_Mon_08_00, activeTemplate_Mon_08_15)),
                new HashSet<Template>(Arrays.asList(activeTemplate_Tue_11_00, activeTemplate_Tue_12_00)),
                new HashSet<Template>(Arrays.asList(activeTemplate_Wed_11_00, inactiveTemplate)),
                new HashSet<Template>(Arrays.asList(activeTemplate_Thu_11_00)),
                new HashSet<Template>(Arrays.asList(activeTemplate_Fri_11_00)),
                new HashSet<Template>(Arrays.asList(activeTemplate_Sat_11_00))
        };
      //  mockTemplatesArr
       //mockClerk.getOffice().getTemplates();
    }

    @Test
    @DisplayName("@field active=false should prevent generating appointments")
    void testUnleashTemplates() {
        Set<Template> set1 = Collections.singleton(activeTemplate_Mon_00_00);
        Set<Template> set2 = Collections.singleton(inactiveTemplate);
        Generator generator = new Clerk();
        generator.setOffice(new Office((Clerk)generator,new OfficeManager()));
        assertEquals(0, generator.getOffice().getAppointments().size(),
                "Set of Appointments is not empty");
        ((Clerk) generator).initSchedule(set1);
        ((Clerk) generator).initSchedule(set2);
        assertEquals(1, generator.getOffice().getAppointments().size(),
                "Set of Appointments in office has wrong size");
    }

    /* "24h Method":
    creates one appointment per template for the following date:
     'lastUpdate' + 1 day + weeks x 7 days (depending on @param weeks, e.g. 1+2x7 ).
      */
    @Test
    void testGenerateAppsOfDay() {
        //mock lastUpdate
        Set<Appointment> appointments = new HashSet<>();
        Instant mockLastUpdate = Instant.parse("2022-01-02T00:00:00.00Z"); //Sunday
        Instant originalLU = Supervisor.getInstance().getLastUpdate();
        //core
        appointments.addAll(mockClerk.generateAppsOfDay(mockLastUpdate, mockTemplatesArr));
        //size of appointments == size of templateArray[weekday]
        System.out.println(appointments);
        int expected = mockTemplatesArr[1].size();// all templates of monday
        int actual = appointments.size();
        assertEquals(expected, actual, "size of appointments created does not match number of templates for mondays");
        Supervisor.getInstance().setLastUpdate(originalLU);

        //each appointment == lastUpdate + 1 day + weeks x 7days
        LocalDate ldLastUpd = LocalDate.ofInstant(mockLastUpdate, mockClerk.getOffice().getOffice_zoneId());
        LocalDate expectedLd = ldLastUpd.plus((1 + (mockClerk.getWeeks() * 7)), ChronoUnit.DAYS);
        for (Appointment a:appointments) {
            LocalDate actualAppLd = a.getDateTime().toLocalDate();
            assertEquals(expectedLd, actualAppLd, "appointment has wrong date");
        }
    }//end testGenerateAppsOfDay

    //entfernen?
    //time of lastTemplateOfDay == time of last appointment of Day
    @Test
    void testFindLastTemplate() {
        mockClerk.findLastTemplate(activeTemplate_Tue_12_00.getStartTime());
        mockClerk.findLastTemplate(activeTemplate_Mon_00_00.getStartTime());
        mockClerk.findLastTemplate(activeTemplate_Tue_11_00.getStartTime());
        assertTrue(mockClerk.getLastTemplateOfDay().equals(LocalTime.of(12,00)));
    }



    public static Stream<Arguments> provideZonedDateTimes(){
        ZonedDateTime monday8h = ZonedDateTime.of(2020,06,01,
                8,0,0,0, ZoneId.of("Europe/Vienna"));
        ZonedDateTime tuesday16h = ZonedDateTime.of(2020,06,02,
                16,0,0,0, ZoneId.of("Europe/Vienna"));
        ZonedDateTime friday23_59 = ZonedDateTime.of(2020,05,29,
                23,59,0,0, ZoneId.of("Europe/Vienna"));
        ZonedDateTime saturday0h = ZonedDateTime.of(2020,05,30,
                0,0,0,0, ZoneId.of("Europe/Vienna"));
        ZonedDateTime sunday01h = ZonedDateTime.of(2020,05,31,
                01,0,0,0, ZoneId.of("Europe/Vienna"));

        return Stream.of(
                Arguments.of(monday8h, 2),
                Arguments.of(tuesday16h, 3),
                Arguments.of(friday23_59, 6),
                Arguments.of(saturday0h, 0),
                Arguments.of(sunday01h, 1)
        );
    }

    //Input: monday 8h --> Out: tuesday=2
    //In: tuesday 16h --> Out wednesday=3
    //In friday 23.59h --> Out saturday=6
    //In saturday 00.00h --> Out sunday=0(!)
    //In sunday 01.00 ---> out: monday=1
    @DisplayName("Numbers representing the following weekday should be returned")
    @ParameterizedTest
    @MethodSource("provideZonedDateTimes")
    void testFindWeekdayFollowingLastUpdate(ZonedDateTime input, int expectedWeekday) {
        int actualWeekday = mockClerk.findWeekdayFollowingLastUpdate(input);

        assertEquals(expectedWeekday, actualWeekday);
    }


    public static Stream<Arguments> provideArgsToTestCatchUp(){
        Instant lU_Sun_22_01_02_T_0000 = ZonedDateTime.of
                        (2022,01,02,0,0,0,0,ZoneId.of("Europe/Vienna")).
                        toInstant();//Sunday 00:00 in Europe/Vienna Summertime
        LocalDateTime now_Fri_22_01_07___2359 = LocalDateTime.of(2022,01,07,23,59,59,0); //Friday
        int exp8 = 8;

        //Instant lU_Sun_22_01_02_T_1030 = Instant.parse("2022-01-02T10:30:00.00Z"); //Sunday 12:30 in Europe/Vienna Summertime
        LocalDateTime now_Tue_22_01_11___2359 = ZonedDateTime.of
                        (2022, 1,11,23,59,59,0,ZoneId.of("Europe/Vienna")).
                        toLocalDateTime(); //Tuesday
        int exp15 = 15;


        Instant lU_Mon_22_01_10_T_2359 = ZonedDateTime.of
                        (2022,01,10,23,59,0,0,ZoneId.of("Europe/Vienna")).
                        toInstant();//Monday 23:59 in Europe/Vienna Summertime
        //LocalDateTime now_Tue_22_01_11___2359 = LocalDateTime.of(2022,01,11,23,59,59,0); //Tuesday
        int exp2 = 2;

        return Stream.of(
                Arguments.of(lU_Sun_22_01_02_T_0000, now_Fri_22_01_07___2359, exp8),
                Arguments.of(lU_Sun_22_01_02_T_0000, now_Tue_22_01_11___2359, exp15),
                Arguments.of(lU_Mon_22_01_10_T_2359, now_Tue_22_01_11___2359, exp2)
        );
    }

    //-give one or two due templates/appointments per weekday

    //-count weekdays between lastUpdate and today/ count number of active templates
    // between lastUpdate(excl) and today(incl)
    //-assertEquals number of appointments and this count
    //-assert lastUpdate has same date and time as last template from yesterday.
    @ParameterizedTest
    @MethodSource("provideArgsToTestCatchUp")
    @DisplayName("compares number of templates and generated appointments in specified period of time" +
            " AND checks correct setting of Supervisor.lastUpdate")
    void testCatchUp(Instant mockLastUpdate, LocalDateTime mockNow, int expectedApps ) {
        //ich suche offset "Vienna" zu Instant(entspricht wohl Greenwich Mean Time, jetzt Koordinierte Weltzeit = UTC)
        ZoneOffset offset = myZoneId.getRules().getOffset(mockNow);
        Instant mockNowInst = mockNow.toInstant(offset);

        Instant originalLU = Supervisor.getInstance().getLastUpdate();
        if (originalLU == null) {
            originalLU = LocalDateTime.of(1900, 01,01,00,00,00,00)
                    .toInstant(offset);
        }
        Supervisor.getInstance().setLastUpdate(mockLastUpdate);

        System.out.println("LOG: originalLU: " + LocalDateTime.ofInstant(originalLU, myZoneId));
        System.out.println("LOG: mockNow: " + mockNow);
        //core
        int actualApps = mockClerk.catchUp(mockNowInst, mockTemplatesArr).size();

        assertEquals(expectedApps, actualApps, "Incorrect number of generated Appointments");
        System.out.println("LOG: expectedApps: " + expectedApps + "\n actualApps: " + actualApps);
        //
        Instant instLastUpdAfter = Supervisor.getInstance().getLastUpdate();

        LocalDateTime expected = LocalDateTime.of(mockNow.toLocalDate(), LocalTime.MIN );
        LocalDateTime actual = LocalDateTime.ofInstant(instLastUpdAfter, myZoneId);

        assertEquals(expected, actual, "Setting of Supervisor.lastUpdate incorrect");
        System.out.println("LOG:\nexpected lastUpdate: " + expected + "\nactual lastUpdate: " + actual);
        //
        Supervisor.getInstance().setLastUpdate(originalLU);
    }
}