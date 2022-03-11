package uk.ac.man.cs.eventlite.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import uk.ac.man.cs.eventlite.EventLite;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EventLite.class)
@DirtiesContext
@ActiveProfiles("test")
public class EventServiceTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	@Autowired
	private EventService eventService;
	
	@Autowired
	private VenueService venueService;
	
	private Event event = new Event();
	
	@BeforeTestClass
	public void initialise() {
		Venue venue = new Venue();
		venue.setId(4);
        venue.setName("Temporary Venue");
        venue.setCapacity(1250);
        venueService.save(venue);
        event.setId(4);
        event.setName("Temporary Event");
        event.setVenue(venue);
        event.setTime(LocalTime.now());
        event.setDate(LocalDate.now());
        event.setDescription("This is just for a test");
	}
	
	@Test
	public void countEventTest() {
		assertEquals(eventService.count(), 3);
		return;
	}
	
	@Test
	public void saveEventTest() {
		eventService.save(this.event);
		assertEquals(eventService.count(), 4);
		Event testEvent = eventService.findById(this.event.getId()).get();
		assertEquals(testEvent.getName(), this.event.getName());
		assertEquals(testEvent.getVenue(), this.event.getVenue());
		assertEquals(testEvent.getDate(), this.event.getDate());
		assertEquals(testEvent.getTime(), this.event.getTime());
		return;		
	}
	
//	@Test
//	public void deleteEventTest() {
//		eventService.deleteById(this.event.getId());
//		assertFalse(eventService.findById(event.getId()).isPresent());
//		return;
//	}
}
