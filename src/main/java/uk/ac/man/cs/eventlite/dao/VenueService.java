package uk.ac.man.cs.eventlite.dao;

import java.util.Optional;

import uk.ac.man.cs.eventlite.entities.Venue;

public interface VenueService {

	public long count();

	public Iterable<Venue> findAll();
	
	public void save(Venue venue);
	
	public Iterable<Venue> listAll(String keyword);
	
	public Optional<Venue> findById(long id);
}
