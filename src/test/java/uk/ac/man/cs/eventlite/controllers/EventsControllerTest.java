package uk.ac.man.cs.eventlite.controllers;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import uk.ac.man.cs.eventlite.config.Security;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EventsController.class)
@Import(Security.class)
public class EventsControllerTest {

	@Autowired
	private MockMvc mvc;

	@Mock
	private Event event;

	@Mock
	private Venue venue;

	@MockBean
	private EventService eventService;

	@MockBean
	private VenueService venueService;

	@Test
	public void getIndexWhenNoEvents() throws Exception {
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());

		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findAll();
		verifyNoInteractions(event);
	}

	@Test
	public void getIndexWithEvents() throws Exception {
		when(event.getVenue()).thenReturn(venue);
		when(eventService.findAll()).thenReturn(Collections.<Event>singletonList(event));

		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findAll();
	}

	@Test
	public void getEventNotFound() throws Exception {
		mvc.perform(get("/events/99").accept(MediaType.TEXT_HTML)).andExpect(status().isNotFound())
				.andExpect(view().name("events/not_found")).andExpect(handler().methodName("getEvent"));
	}
	
	@Test
	public void getEventFound () throws Exception {
		Event e = new Event();
		e.setId(25);
		e.setName("event");
		e.setVenue(venue);
		e.setTime(LocalTime.MIDNIGHT);
		e.setDate(LocalDate.now());
		
		when(eventService.findById(25)).thenReturn(Optional.of(e));
		
		mvc.perform(get("/events/25").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("events/details")).andExpect(handler().methodName("getEvent"))
		.andExpect(model().hasNoErrors()).andExpect(model().attribute("event", e));
		
		verify(eventService).findById(25);
	}
	
	@Test
	public void createNewEvenWithNoVenue() throws Exception {		
		mvc.perform(post("/events/new").with(user("Sam").roles(Security.ORGANIZER_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "event")
				.param("description", "this is an test event")
				.param("venue", "")
				.param("date", "2022-06-10")
				.param("time", "23:17")				
				.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
				.andExpect(view().name("events/new")).andExpect(model().hasErrors())
				.andExpect(handler().methodName("createEvent"));

	}
	
	@Test  // Currently fails, so needs fixing
	public void createNewEventSuccess() throws Exception {
		ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
		doNothing().when(eventService).save(any(Event.class));
		doNothing().when(event).setVenue(any(Venue.class));
		
		mvc.perform(post("/events/new").with(user("Sam").roles(Security.ORGANIZER_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "event")
				.param("description", "this is an test event")
				.param("venue", String.valueOf(venue))
				.param("date", "2022-06-10")
				.param("time", "23:17")				
				.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
				.andExpect(view().name("redirect:/events")).andExpect(model().hasNoErrors())
				.andExpect(handler().methodName("createEvent"));

		verify(eventService).save(arg.capture());
		assertEquals("event", arg.getValue().getName());
	}
	
	
}
