package uk.ac.man.cs.eventlite.dao;

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
		return eventRepository.findAll(); 
	}
	
	@Override
	public void save(Event event) {
		eventRepository.save(event);
	}
	
	@Override
	public Iterable<Event> listAll(String keyword){
		if (keyword != null) {
			return eventRepository.search(keyword);
		}
		return eventRepository.findAll();
	}
}
