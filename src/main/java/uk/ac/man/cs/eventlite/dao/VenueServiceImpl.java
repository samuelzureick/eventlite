package uk.ac.man.cs.eventlite.dao;

import java.util.Optional;

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
		return findAllByOrderByName();
	}
	
	@Override
	public Iterable<Venue> findAllByOrderByName() {
		return venueRepository.findAllByOrderByName();
	}

	@Override
	public void save(Venue venue) {
		venueRepository.save(venue);
	}

	@Override
	public Iterable<Venue> listAll(String keyword) {
		if (keyword != null) {
			return venueRepository.search(keyword);
		}
		return venueRepository.findAll();
	}

	@Override
	public Optional<Venue> findById(long id) {
		return venueRepository.findById(id);
	}

	@Override
	public Iterable<Venue> listVenuesOrderByEventsNumber() {
		return venueRepository.searchVenuesOrderByEventsNumber();
	}
}
