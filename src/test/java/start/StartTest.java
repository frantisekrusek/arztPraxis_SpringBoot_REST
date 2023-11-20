package start;

import model.appointment.Template;
import model.generator.Supervisor;
import model.generator.updater.Clerk;
import model.office.Office;
import model.person.officeManager.OfficeManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


class StartTest {

    Clerk clerk;
    Office office;
    OfficeManager officeManager = new OfficeManager();


    @BeforeEach
    void setUp() {
        this.clerk = new Clerk();
        this.officeManager = new OfficeManager();
        this.office = new Office(this.clerk, this.officeManager);

        this.clerk = (Clerk)office.getGenerator();
        Template activeTemplate_Mon_00_00, activeTemplate_Mon_08_00, activeTemplate_Mon_08_15,
                activeTemplate_Tue_11_00, activeTemplate_Tue_12_00,
                activeTemplate_Wed_11_00,
                activeTemplate_Tue_23_00,
                activeTemplate_Thu_11_00,
                activeTemplate_Fri_11_00,
                activeTemplate_Sat_11_00,
                activeTemplate_Sun_07_00, inactiveTemplate;
        activeTemplate_Mon_00_00 = new Template(DayOfWeek.MONDAY, LocalTime.MIDNIGHT); activeTemplate_Mon_00_00.setActive(true);
        activeTemplate_Mon_08_00 = new Template(DayOfWeek.MONDAY, LocalTime.of(8,00)); activeTemplate_Mon_08_00.setActive(true);
        activeTemplate_Mon_08_15 = new Template(DayOfWeek.MONDAY, LocalTime.of(8,15)); activeTemplate_Mon_08_15.setActive(true);
        activeTemplate_Tue_11_00 = new Template(DayOfWeek.TUESDAY, LocalTime.of(11,00)); activeTemplate_Tue_11_00.setActive(true);
        activeTemplate_Tue_12_00 = new Template(DayOfWeek.TUESDAY, LocalTime.of(12,00)); activeTemplate_Tue_12_00.setActive(true);
        activeTemplate_Tue_23_00 = new Template(DayOfWeek.TUESDAY, LocalTime.of(23,00)); activeTemplate_Tue_23_00.setActive(true);
        activeTemplate_Wed_11_00 = new Template(DayOfWeek.WEDNESDAY, LocalTime.of(11,00)); activeTemplate_Wed_11_00.setActive(true);
        activeTemplate_Thu_11_00 = new Template(DayOfWeek.THURSDAY, LocalTime.of(11,00)); activeTemplate_Thu_11_00.setActive(true);
        activeTemplate_Fri_11_00 = new Template(DayOfWeek.FRIDAY, LocalTime.of(11,00)); activeTemplate_Fri_11_00.setActive(true);
        activeTemplate_Sat_11_00 = new Template(DayOfWeek.SATURDAY, LocalTime.of(11,00)); activeTemplate_Sat_11_00.setActive(true);
        activeTemplate_Sun_07_00 = new Template(DayOfWeek.SUNDAY, LocalTime.of(7,00)); activeTemplate_Sun_07_00.setActive(true);
        inactiveTemplate = new Template(DayOfWeek.WEDNESDAY, LocalTime.NOON);
        Set[] newTemplates = new Set[]{
                new HashSet<Template>(Arrays.asList(activeTemplate_Sun_07_00)),
                new HashSet<Template>(Arrays.asList(activeTemplate_Mon_00_00, activeTemplate_Mon_08_00, activeTemplate_Mon_08_15)),
                new HashSet<Template>(Arrays.asList(activeTemplate_Tue_11_00, activeTemplate_Tue_12_00, activeTemplate_Tue_23_00)),
                new HashSet<Template>(Arrays.asList(activeTemplate_Wed_11_00, inactiveTemplate)),
                new HashSet<Template>(Arrays.asList(activeTemplate_Thu_11_00)),
                new HashSet<Template>(Arrays.asList(activeTemplate_Fri_11_00)),
                new HashSet<Template>(Arrays.asList(activeTemplate_Sat_11_00))};

        office.mergeTemplates(newTemplates);
    }

    @Test
    @DisplayName("Erste Inbetriebnahme: Einige Templates wurden erstellt. Ab (inkl) heute" +
            "sollen daraus für 3 Wochen im Voraus Termine erzeugt werden.")
    void simulation1(){
        clerk.setWeeks(3);
        int before = clerk.getOffice().getAppointments().size();
        clerk.unleashTemplates(office.getTemplates());
        //Ergebnisse
        System.out.println(clerk.sortAppointments());
        int after = clerk.getOffice().getAppointments().size();
        int actual = after - before;
        int expected = 3*11;
        assertEquals(expected, actual, "mismatch in number of newly created appointments");
    }


    public static Stream<Arguments> provideArgsToSimulation2() {

                        //weeks, minusdays, expectedCreatedAppointments
        return Stream.of(
                Arguments.of(1,14,22),
                Arguments.of(1,0,0)
        );
    }
    
    
    @ParameterizedTest
    @MethodSource("provideArgsToSimulation2")
    @DisplayName("System wird nach einigen Wochen oder Tagen Auszeit neu gestartet")
    void simulation2(int weeks, int minusDays, int appointmentsCreatedExpectedSize ){
        clerk.setWeeks(weeks);
        LocalDateTime someDateBefore = LocalDateTime.now().minusDays(minusDays).with(LocalTime.MIN);
        
        System.out.println("LOG: " + someDateBefore);

        Instant originalLastUpdate = Supervisor.getInstance().getLastUpdate();
        Supervisor.getInstance().setLastUpdate(someDateBefore.toInstant(office.getOffset()));
        Instant lastUpdate = Supervisor.getInstance().getLastUpdate();
                System.out.println("LOG lastUpdate before : " +
                LocalDateTime.ofInstant(lastUpdate,office.getOffice_zoneId()));
        //core
        int actualSize = clerk.catchUp(Instant.now(), office.getTemplates()).size();

        System.out.println("LOG lastUpdate after : " + LocalDateTime.ofInstant(Supervisor.getInstance().getLastUpdate(),office.getOffice_zoneId()));


        assertEquals(appointmentsCreatedExpectedSize,actualSize,"incorrect number of appointments created");

        Supervisor.getInstance().setLastUpdate(originalLastUpdate);
    }

}