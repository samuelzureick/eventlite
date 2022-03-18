package uk.ac.man.cs.eventlite.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import uk.ac.man.cs.eventlite.entities.Venue;

public interface VenueRepository extends CrudRepository<Venue, Long> {

	@Query("SELECT v FROM Venue v WHERE v.name LIKE %?1% OR LOWER(v.name) LIKE %?1% ORDER BY v.name")
	List<Venue> search(String keyword);
	
	List<Venue> findAllByOrderByName();

	@Query("SELECT e.venue FROM Event e GROUP BY e.venue ORDER BY COUNT(*) DESC")
	List<Venue> searchVenuesOrderByEventsNumber();
}
