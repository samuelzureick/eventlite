package uk.ac.man.cs.eventlite.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import uk.ac.man.cs.eventlite.EventLite;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EventLite.class)
@DirtiesContext
@ActiveProfiles("test")
public class VenueServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private VenueService venueService;

//	@BeforeAll
//	public void initialise() {
//		Venue venue = new Venue();
//        venue.setName("Venue For Test");
//        venue.setAddress("Stuart Road WA15 8QY");
//        venue.setCapacity(1250);
//        venueService.save(venue);
//        Event event = new Event();
//        event.setName("Event For Test");
//        event.setVenue(venue);
//        event.setTime(LocalTime.now());
//        event.setDate(LocalDate.now());
//        event.setDescription("This is just an event for test.");
//	}
	
	@Test
	public void countVenueTest() {
		assertEquals(venueService.count(), 3);
		return;
	}


}
