package uk.ac.man.cs.eventlite.dao;

import java.util.Optional;

import uk.ac.man.cs.eventlite.entities.Venue;

public interface VenueService {

	public long count();

	public Iterable<Venue> findAll();

	public Iterable<Venue> findAllByOrderByName();

	public void save(Venue venue);

	public void deleteById(long id);
	
	public Iterable<Venue> listAll(String keyword);

	public Optional<Venue> findById(long id);

	public Iterable<Venue> listVenuesOrderByEventsNumber();
}
