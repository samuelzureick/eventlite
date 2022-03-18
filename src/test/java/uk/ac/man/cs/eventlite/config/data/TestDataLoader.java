package uk.ac.man.cs.eventlite.config.data;

import java.time.LocalDate;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import uk.ac.man.cs.eventlite.dao.EventService;
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

    @Bean
    CommandLineRunner initDatabase() {
		return args -> {
			Venue venue1 = null, venue2 = null, venue3 = null;

			if (venueService.count() > 0) {
				log.info("Database already populated with venues. Skipping venue initialization.");
			} else {
	            // Build and save initial venues here.
	            venue2 = new Venue();
	            venue2.setName("Venue B");
	            venue2.setAddress("Highland Road S43 2EZ");
	            venue2.setCapacity(1000);
	            venueService.save(venue2);
	            venue3 = new Venue();
	            venue3.setName("Venue C");
	            venue3.setAddress("19 Acacia Avenue WA15 8QY");
	            venue3.setCapacity(10);
	            venueService.save(venue3);
	            venue1 = new Venue();
	            venue1.setName("Venue A");
	            venue1.setAddress("23 Manchester Road E14 3BD");
	            venue1.setCapacity(50);
	            venueService.save(venue1);
			}

			if (eventService.count() > 0) {
				log.info("Database already populated with events. Skipping event initialization.");
			} else {
	            // Build and save initial events here.
	            Event event1 = new Event();
	            event1.setName("Event Alpha");
	            event1.setVenue(venue2);
	            event1.setTime(LocalTime.now());
	            event1.setDate(LocalDate.now());
	            event1.setDescription("One very cool event. Bring your friends!");
	            eventService.save(event1);
	            Event event2 = new Event();
	            event2.setName("Event Beta");
	            event2.setVenue(venue1);
	            event2.setTime(LocalTime.now().plusHours(1));
	            event2.setDate(LocalDate.now().plusDays(1));
	            event2.setDescription("Another very cool event. Bring your friends two!");
	            eventService.save(event2);
	            Event event3 = new Event();
	            event3.setName("Event Apple");
	            event3.setVenue(venue1);
	            event3.setTime(LocalTime.now().plusHours(-2));
	            event3.setDate(LocalDate.now().plusDays(-2));
	            event3.setDescription("The last very cool event. Three your friends!");
	            eventService.save(event3);
			}
        };
    }
}