package controller;

import model.appointment.Template;
import model.generator.updater.Clerk;
import model.office.Office;
import model.person.officeManager.OfficeManager;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Scanner;
import java.util.Set;

public class Controller {

    OfficeManager officeManager;
    Office office;
    Clerk clerk;
    Template template;


    public Controller() {
        this.officeManager = new OfficeManager();
        this.clerk = new Clerk();
        this.office = new Office(clerk, officeManager);
    }

    public void selectUser(){
        System.out.println(
                "Select user by entering number\n" +
                        "1 - Office Manager\n");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        switch (choice){
            case 1: selectAction_forOfficeManager();
            break;
        }
    }//end selectUser()

    public void selectAction_forOfficeManager(){
        System.out.println(
                "Select action for Office Manager by entering number\n" +
                        "1 - create NEW TEMPLATE for appointment calendar\n" +
                        "0 - EXIT");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        switch (choice){
            case 1: template = this.selectDayOfWeek();
            this.officeManager.createTemplate(template.getWeekday(), template.getStartTime());
                System.out.println("LOG Controller selectAction_forOfficeManager: \n");
                for (Set<Template> ts:officeManager.getOffice().getTemplates()){
                    System.out.println(ts.toString());
                }
                this.selectAction_forOfficeManager();
                break;
            case 0: System.exit(0);break;
        }
    }//end selectAction_forOfficeManager()

    public Template selectDayOfWeek(){
        DayOfWeek dayOfWeek = null;
        System.out.println(
                "Select weekday for template\n" +
                        "1 - Monday\n" +
                        "2 - Tuesday\n" +
                        "3 - Wednesday\n" +
                        "4 - Thursday\n" +
                        "5 - Friday\n" +
                        "6 - Saturday\n" +
                        "7 - Sunday\n");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        switch (choice){
            case 1: dayOfWeek = DayOfWeek.MONDAY;break;
            case 2: dayOfWeek = DayOfWeek.TUESDAY;break;
            case 3: dayOfWeek = DayOfWeek.WEDNESDAY;break;
            case 4: dayOfWeek = DayOfWeek.THURSDAY;break;
            case 5: dayOfWeek = DayOfWeek.FRIDAY;break;
            case 6: dayOfWeek = DayOfWeek.SATURDAY;break;
            case 7: dayOfWeek = DayOfWeek.SUNDAY;break;
        }
        return this.selectLocalTime(dayOfWeek);
    }//end selectDayOfWeek

    public Template selectLocalTime(DayOfWeek dayOfWeek){
        LocalTime localTime = null;
        System.out.println(
                "Select hour of day for template\n");
        Scanner scanner = new Scanner(System.in);
        int hour = scanner.nextInt();

        System.out.println(
                "Select minutes for template\n");
        scanner = new Scanner(System.in);
        int minutes = scanner.nextInt();

        localTime = LocalTime.of(hour, minutes);
        template = new Template(dayOfWeek, localTime);
        return template;
    }//end selectLocalTime

    //todo: public void _24hMethode()

}//end class
