package service;

import exceptions.SupervisorNotFoundException;
import jakarta.persistence.Id;
import model.Supervisor;
import model.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.TimerTask;

public class Task extends TimerTask {

    private Clerk clerk;
    private Office office;
    private JpaRepository<Supervisor,Long> supervisorRepo;
    private JpaRepository<Template,Long> templateRepo;


    public Task(Clerk clerk, Office office,
                JpaRepository<Supervisor, Long> supervisorRepo,
                JpaRepository<Template, Long> templateRepo){
        this.clerk = clerk;
        this.office = office;
        this.supervisorRepo = supervisorRepo;
        this.templateRepo = templateRepo;
    }

    @Override
    public void run() {
        //System.out.println("Die Zeit: " + LocalDateTime.now());
        Supervisor supervisor = supervisorRepo.findById(1L).orElseThrow(() -> new SupervisorNotFoundException(1L));
        Instant lastUpdate = supervisor.getLastUpdate();
        List<Template> templateList = templateRepo.findAll();
        clerk.generateAppsOfDay(lastUpdate, office.fromListTo_ArrayOfSets(templateList));
    }
}
