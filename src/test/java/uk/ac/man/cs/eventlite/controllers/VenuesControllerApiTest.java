package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import uk.ac.man.cs.eventlite.assemblers.EventModelAssembler;
import uk.ac.man.cs.eventlite.assemblers.VenueModelAssembler;
import uk.ac.man.cs.eventlite.config.Security;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VenuesControllerApi.class)
@Import({ Security.class, VenueModelAssembler.class, EventModelAssembler.class })
public class VenuesControllerApiTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private VenueService venueService;

	@MockBean
	private EventService eventService;

	@Test
	public void getIndexWhenNoVenues() throws Exception {
		when(venueService.findAll()).thenReturn(Collections.<Venue>emptyList());

		mvc.perform(get("/api/venues").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getAllVenues")).andExpect(jsonPath("$.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues")))
				.andExpect(jsonPath("$._links.profile.href", endsWith("/api/profile/venues")));

		verify(venueService).findAll();
	}

	@Test
	public void getIndexWithVenues() throws Exception {
		Venue v = new Venue();
		v.setId(0);
		v.setName("Venue");
		v.setCapacity(1000);
		v.setAddress("176 Oxford Rd, Manchester M13 9PL");
		when(venueService.findAll()).thenReturn(Collections.<Venue>singletonList(v));

		mvc.perform(get("/api/venues").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getAllVenues")).andExpect(jsonPath("$.length()", equalTo(2)))
				.andExpect(jsonPath("$._embedded.venues.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues")))
				.andExpect(jsonPath("$._links.profile.href", endsWith("/api/profile/venues")));

		verify(venueService).findAll();
	}

	@Test
	public void getVenueNotFound() throws Exception {
		mvc.perform(get("/api/venues/99").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error", containsString("venue 99"))).andExpect(jsonPath("$.id", equalTo(99)))
				.andExpect(handler().methodName("getVenue"));
	}

	@Test
	public void getVenue() throws Exception {
		Venue v = new Venue();
		v.setId(0);
		v.setName("Venue");
		v.setCapacity(1000);
		v.setAddress("176 Oxford Rd, Manchester M13 9PL");
		when(venueService.findById(0)).thenReturn(Optional.of(v));

		mvc.perform(get("/api/venues/0").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getVenue"))
				.andExpect(jsonPath("$.id", equalTo(0)))
				.andExpect(jsonPath("$.name", equalTo("Venue")))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues/0")))
				.andExpect(jsonPath("$._links.venue.href", endsWith("/api/venues/0")))
				.andExpect(jsonPath("$._links.events.href", endsWith("/api/venues/0/events")))
				.andExpect(jsonPath("$._links.next3events.href", endsWith("/api/venues/0/next3events")));

		verify(venueService).findById(0);
	}

	@Test
	public void getRelatedEvents() throws Exception {
		Venue v = new Venue();
		v.setId(0);
		v.setName("Venue");
		v.setCapacity(1000);
		v.setAddress("176 Oxford Rd, Manchester M13 9PL");
		Event e = new Event();
		e.setId(1);
		e.setName("Event");
		e.setDate(LocalDate.now());
		e.setTime(LocalTime.now());
		e.setVenue(v);
		when(venueService.findById(0)).thenReturn(Optional.of(v));
		when(eventService.findAll()).thenReturn(Collections.<Event>singletonList(e));

		mvc.perform(get("/api/venues/0/events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getRelatedEvents"))
				.andExpect(jsonPath("$.length()", equalTo(2)))
				.andExpect(jsonPath("$._embedded.events.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues/0/events")));

		verify(venueService).findById(0);
		verify(eventService).findAll();
	}

	@Test
	public void getNext3Events() throws Exception {
		Venue v = new Venue();
		v.setId(0);
		v.setName("Venue");
		v.setCapacity(1000);
		v.setAddress("176 Oxford Rd, Manchester M13 9PL");
		Event e = new Event();
		e.setId(1);
		e.setName("Event");
		e.setDate(LocalDate.now().plusDays(10));
		e.setTime(LocalTime.now());
		e.setVenue(v);
		when(venueService.findById(0)).thenReturn(Optional.of(v));
		when(eventService.findAll()).thenReturn(Collections.<Event>singletonList(e));
		List<Event> events = new ArrayList<Event>();
		events.add(e);
		when(eventService.splitEventFuture(events)).thenReturn(events);

		mvc.perform(get("/api/venues/0/next3events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getNext3Events"))
				.andExpect(jsonPath("$.length()", equalTo(2)))
				.andExpect(jsonPath("$._embedded.events.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues/0/next3events")));

		verify(venueService).findById(0);
		verify(eventService).findAll();
		verify(eventService).splitEventFuture(events);
	}

}
