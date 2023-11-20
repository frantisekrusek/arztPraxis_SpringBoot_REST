package model.office;

import model.generator.Supervisor;
import model.generator.updater.Clerk;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.TimerTask;

public class Task extends TimerTask {

    private Clerk clerk;

    public Task(Clerk clerk){
        this.clerk = clerk;
    }

    @Override
    public void run() {
        //System.out.println("Die Zeit: " + LocalDateTime.now());
        clerk.generateAppsOfDay(Supervisor.getInstance().getLastUpdate(), clerk.getOffice().getTemplates());
    }
}
