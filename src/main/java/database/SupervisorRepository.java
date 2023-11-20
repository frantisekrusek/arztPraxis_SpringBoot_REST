package database;

import model.generator.Supervisor;
import org.springframework.data.repository.CrudRepository;

public interface SupervisorRepository extends CrudRepository<Supervisor, Integer>
{
}
