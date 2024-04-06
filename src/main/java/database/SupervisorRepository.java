package database;

import model.generator.Supervisor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface SupervisorRepository extends JpaRepository<Supervisor, Long> {
}
