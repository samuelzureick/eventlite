package uk.ac.man.cs.eventlite.dao;

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
		return findAllByOrderByDateTime(); 
	}
	
	@Override
	public void save(Event event) {
		eventRepository.save(event);
	}
	
	@Override
	public Iterable<Event> findAllByOrderByDateTime(){
		Iterable<Event> events = eventRepository.findAll();
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
	
}
