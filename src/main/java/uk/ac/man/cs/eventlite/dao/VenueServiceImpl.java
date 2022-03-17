package uk.ac.man.cs.eventlite.dao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.man.cs.eventlite.entities.Venue;

@Service
public class VenueServiceImpl implements VenueService {

	@Autowired
	private VenueRepository venueRepository;

	@Override
	public long count() {
		return venueRepository.count();
	}

	@Override
	public Iterable<Venue> findAll() {
		Iterable<Venue> venues = venueRepository.findAll();
		return orderByName(venues);
	}

	@Override
	public void save(Venue venue) {
		venueRepository.save(venue);
	}

	@Override
	public Iterable<Venue> listAll(String keyword) {
		Iterable<Venue> venues;
		if (keyword != null) {
			venues = venueRepository.search(keyword);
		} else {
			venues = venueRepository.findAll();
		}
		return orderByName(venues);
	}

	@Override
	public Optional<Venue> findById(long id) {
		return venueRepository.findById(id);
	}

	@Override
	public Iterable<Venue> orderByName(Iterable<Venue> venues) {
		//Convert to List
		List<Venue> list = StreamSupport
				  .stream(venues.spliterator(), false)
				  .collect(Collectors.toList());
		//Sort List
		list.sort((a, b) -> (a.getName().compareTo(b.getName())));
		return list;
	}
}
