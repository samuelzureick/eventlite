package uk.ac.man.cs.eventlite.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import uk.ac.man.cs.eventlite.EventLite;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EventLite.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class EventServiceTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	@Autowired
	private EventService eventService;
	
	@Autowired
	private VenueService venueService;
		
	private Event event = new Event();
	
	private Venue venue = new Venue();
	
	@BeforeEach
	public void initialise() {
        venue.setName("Temporary Venue");
        venue.setAddress("Stuart Road WA15 8QY");
        venue.setCapacity(1250);
        venueService.save(venue);
        event.setName("Temporary Event");
        event.setVenue(venue);
        event.setTime(LocalTime.now());
        event.setDate(LocalDate.now());
        event.setDescription("This is just for a test");
	}
	
	@AfterEach
	public void tearDown() {
		venueService.deleteById(venue.getId());
	}
	
	@Test
	public void countEventTest() {
		assertEquals(3, eventService.count());
		return;
	}
	
	@Test
	public void findByIdTest() {
		eventService.save(event);
		Event eventUT = eventService.findById(event.getId()).get();
		assertEquals("Temporary Event", eventUT.getName());
		return;
	}
	
	@Test
	public void saveEventTest() {
		eventService.save(event);
		assertEquals(4, eventService.count());
		Event testEvent = eventService.findById(event.getId()).get();
		assertEquals(event.getName(), testEvent.getName());
		assertEquals(event.getVenue(), testEvent.getVenue());
		assertEquals(event.getDate(), testEvent.getDate());
		assertEquals(event.getTime(), testEvent.getTime());
		return;		
	}
	
	@Test
	public void deleteEventTest() {
		eventService.save(event);
		eventService.deleteById(event.getId());
		assertFalse(eventService.findById(event.getId()).isPresent());
		return;
	}
	
	@Test
	public void finalAllEventTest() {
		ArrayList<Event> eventsList = new ArrayList<Event>();
		Iterable<Event> eventsUT = eventService.findAll();
		for (Event e : eventsUT) {
			eventsList.add(e);
			System.out.println(e.getName());
		}
		for (int i=0; i<10; i++) {
			System.out.println("");
		}
		assertEquals("Event Apple", eventsList.get(0).getName());
		assertEquals("Event Alpha", eventsList.get(1).getName());
		assertEquals("Event Beta", eventsList.get(2).getName());
	}
	
	
	
}
