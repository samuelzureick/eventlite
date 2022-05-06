package uk.ac.man.cs.eventlite.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

	@Test
	public void countEventTest() {
		assertEquals(3, eventService.count());
	}

	@Test
	public void findByIdTest() {
		eventService.save(event);
		Event testEvent = eventService.findById(event.getId()).orElse(null);
		assertNotNull(testEvent);
		assertEquals(event.getName(), testEvent.getName());
	}

	@Test
	public void saveEventTest() {
		eventService.save(event);
		assertEquals(4, eventService.count());
		Event testEvent = eventService.findById(event.getId()).orElse(null);
		assertNotNull(testEvent);
		assertEquals(event.getName(), testEvent.getName());
		assertEquals(event.getVenue(), testEvent.getVenue());
		assertEquals(event.getDate(), testEvent.getDate());
		assertEquals(event.getTime(), testEvent.getTime());
	}

	@Test
	public void deleteByIdTest() {
		eventService.save(event);
		eventService.deleteById(event.getId());
		assertFalse(eventService.findById(event.getId()).isPresent());
	}

	@Test
	public void findAllEventsTest() {
		List<Event> eventsList = new ArrayList<Event>();
		Iterable<Event> eventsUT = eventService.findAll();
		for (Event e : eventsUT) {
			eventsList.add(e);
		}
		assertEquals("Event Apple", eventsList.get(0).getName());
		assertEquals("Event Alpha", eventsList.get(1).getName());
		assertEquals("Event Beta", eventsList.get(2).getName());
	}

	@Test
	public void splitEventTest() {
		Event futureEvent = new Event();
		futureEvent.setName("Future Event");
		futureEvent.setVenue(venue);
		futureEvent.setTime(LocalTime.now());
		futureEvent.setDate(LocalDate.now().plusDays(10));
		futureEvent.setDescription("This event is in the future");
		eventService.save(futureEvent);
		Event pastEvent = new Event();
		pastEvent.setName("Past Event");
		pastEvent.setVenue(venue);
		pastEvent.setTime(LocalTime.now());
		pastEvent.setDate(LocalDate.now().minusDays(10));
		pastEvent.setDescription("This event is in the past");
		eventService.save(pastEvent);
		Iterable<Event> allEvents = eventService.findAll();
		List<Event> pastEvents = eventService.splitEventPast(allEvents);
		List<Event> futureEvents = eventService.splitEventFuture(allEvents);
		assertTrue(pastEvents.contains(pastEvent));
		assertFalse(pastEvents.contains(futureEvent));
		assertTrue(futureEvents.contains(futureEvent));
		assertFalse(futureEvents.contains(pastEvent));
	}
	
	@Test
	public void listAllTest() {
		List<Event> eventsList = new ArrayList<Event>();
		Iterable<Event> allEvents = eventService.listAll(null);
		for (Event e : allEvents) {
			eventsList.add(e);
		}
		assertEquals("Event Apple", eventsList.get(0).getName());
		assertEquals("Event Alpha", eventsList.get(1).getName());
		assertEquals("Event Beta", eventsList.get(2).getName());
		eventsList.clear();
		Iterable<Event> searchEvents = eventService.listAll("Apple");
		for (Event e : searchEvents) {
			eventsList.add(e);
		}
		assertEquals("Event Apple", eventsList.get(0).getName());
	}
}
