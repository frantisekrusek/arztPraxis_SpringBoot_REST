package database;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import model.appointment.Appointment;

import java.util.HashMap;
import java.util.Map;

public class CustomAppointmentRepositoryImpl implements CustomAppointmentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Appointment customFindById(Long id){
        EntityGraph graph = this.entityManager.getEntityGraph("graph.Appointment.patient");
        Map hints = new HashMap();
        hints.put("javax.persistence.fetchgraph", graph);

        Appointment appointment = this.entityManager.find(Appointment.class, id, hints);
        //Order order = this.em.find(Order.class, orderId, hints);
        return appointment;
    }

}
