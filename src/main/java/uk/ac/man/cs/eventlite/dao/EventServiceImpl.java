package uk.ac.man.cs.eventlite.dao;

import java.util.Optional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.man.cs.eventlite.entities.Event;

@Service
public class EventServiceImpl implements EventService {

	@Autowired
	private EventRepository eventRepository;

	@Override
	public long count() {
		return eventRepository.count();
	}

	@Override
	public Iterable<Event> findAll() {
		Iterable<Event> events = eventRepository.findAll();
		return orderByDateTime(events); 
	}

	@Override
	public void save(Event event) {
		eventRepository.save(event);
	}

	@Override
	public Optional<Event> findById(long id) {
		return eventRepository.findById(id);
	}

	@Override
	public Iterable<Event> orderByDateTime(Iterable<Event> events) {
		//Convert to List
		List<Event> list = StreamSupport
				  .stream(events.spliterator(), false)
				  .collect(Collectors.toList());
		//Sort List
		list.sort((a, b)
					-> (a.getDate().compareTo(b.getDate()) == 0 ?
							a.getTime().compareTo(b.getTime()) : 
								a.getDate().compareTo(b.getDate())));
		return list;
	}

	@Override
	public Iterable<Event> listAll(String keyword){
		Iterable<Event> events;
		if (keyword != null) {
			events = eventRepository.search(keyword);
		} else {
			events = eventRepository.findAll();
		}
		return orderByDateTime(events);
	}

	@Override
	public void deleteById(long id) {
		eventRepository.deleteById(id);
	}

	@Override
	public List<Event> splitEventPast(Iterable<Event> events){
		List<Event> pastEvents = new ArrayList<Event>();
		LocalDate today = LocalDate.now();
		for (Event event : events) {
			if (event.getDate().isBefore(today)) {
				pastEvents.add(event);
			}
		}
		Collections.sort(pastEvents, (a, b) -> {
			if (a.getDate().compareTo(b.getDate()) != 0) {
				return -a.getDate().compareTo(b.getDate());
			} else {
				return a.getName().compareTo(b.getName());
			}
		});
		return pastEvents;
	}

	@Override
	public List<Event> splitEventFuture(Iterable<Event> events){
		List<Event> futureEvents = new ArrayList<Event>();
		LocalDate today = LocalDate.now();
		for (Event event : events) {
			if (event.getDate().isEqual(today) || event.getDate().isAfter(today)) {
				futureEvents.add(event);
			}
		}
		Collections.sort(futureEvents, (a, b) -> {
			if (a.getDate().compareTo(b.getDate()) != 0) {
				return a.getDate().compareTo(b.getDate());
			} else {
				return a.getName().compareTo(b.getName());
			}
		});
		return futureEvents;
	}
}
