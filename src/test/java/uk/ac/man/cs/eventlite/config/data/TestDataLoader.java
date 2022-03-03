package uk.ac.man.cs.eventlite.config.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import uk.ac.man.cs.eventlite.dao.EventRepository;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueRepository;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Configuration
@Profile("test")
public class TestDataLoader {

	private final static Logger log = LoggerFactory.getLogger(TestDataLoader.class);

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;
	
	@Autowired
	private EventRepository eventRepository;
	
	@Autowired
	private VenueRepository venueRepository;

	@Bean
	CommandLineRunner initDatabase() {
		return args -> {
			if (venueService.count() > 0) {
				log.info("Database already populated with venues. Skipping venue initialization.");
			} else {
				Venue testVenue1 = new Venue();
				testVenue1.setName("Kilburn Building");
				testVenue1.setCapacity(120);
				venueRepository.save(testVenue1);
				Venue testVenue2 = new Venue();
				testVenue2.setName("Online");
				testVenue2.setCapacity(100000);
				venueRepository.save(testVenue2);
			}

			if (eventService.count() > 0) {
				log.info("Database already populated with events. Skipping event initialization.");
			} else {
				Event testEvent1 = new Event();
				testEvent1.setName("COMP23412 Showcase, group F");
				testEvent1.setVenue(1);
				eventRepository.save(testEvent1);
				
				Event testEvent2 = new Event();
				testEvent2.setName("COMP23412 Showcase, group G");
				testEvent2.setVenue(1);
				eventRepository.save(testEvent2);

				Event testEvent3 = new Event();
				testEvent3.setName("COMP23412 Showcase, group H");
				testEvent3.setVenue(1);
				eventRepository.save(testEvent3);

		}
	};
}}
