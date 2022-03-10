package uk.ac.man.cs.eventlite.dao;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import uk.ac.man.cs.eventlite.entities.Event;
public interface EventRepository extends CrudRepository<Event, Long>{

	@Query("SELECT e FROM Event e WHERE e.name LIKE %?1% OR LOWER(e.name) LIKE %?1%")
	List<Event> search(String keyword);
}
