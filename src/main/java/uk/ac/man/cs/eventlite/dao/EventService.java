package uk.ac.man.cs.eventlite.dao;

import java.util.List;
import java.util.Optional;

import uk.ac.man.cs.eventlite.entities.Event;

public interface EventService {

	public long count();

	public Iterable<Event> findAll();

	public void save(Event event);

	public Optional<Event> findById(long id);

	public Iterable<Event> orderByDateTime(Iterable<Event> events);

	public Iterable<Event> listAll(String keyword);

	public void deleteById(long id);

	public void updateEvent(Event event);

	public List<Event> splitEventPast(Iterable<Event> events);

	public List<Event> splitEventFuture(Iterable<Event> events);
}
