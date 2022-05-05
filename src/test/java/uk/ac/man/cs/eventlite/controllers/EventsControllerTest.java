package uk.ac.man.cs.eventlite.controllers;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;

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
	public void newEventPage() throws Exception {
		mvc.perform(get("/events/new").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("events/new")).andExpect(handler().methodName("newEvent"))
		.andExpect(model().hasNoErrors());

	}
	
	
	@Test
	public void createNewEventWithNoVenue() throws Exception {
		ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
		mvc.perform(post("/events/new").with(user("Sam").roles(Security.ORGANIZER_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "event")
				.param("description", "this is an test event")
				.param("venue", "")  // No venue given to the event, violating our constraints
				.param("date", "2022-06-10")
				.param("time", "23:17")				
				.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
				.andExpect(view().name("events/new")).andExpect(model().hasErrors())
				.andExpect(handler().methodName("createEvent"));

		verify(eventService, never()).save(arg.capture());
	}
	
	@Test
	public void createNewEventWithWrongAuthority() throws Exception {
		ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
		
		mvc.perform(post("/events/new").with(user("Sam").roles(Security.ATTENDEE_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "event")
				.param("description", "this is an test event")
				.param("venue.id", "1")
				.param("date", "2022-06-10")
				.param("time", "23:17")				
				.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isForbidden());

		verify(eventService, never()).save(arg.capture());
	}
	
	@Test
	public void createNewEventSuccess() throws Exception {
		ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
		doNothing().when(eventService).save(any(Event.class));
		doNothing().when(event).setVenue(any(Venue.class));
		
		mvc.perform(post("/events/new").with(user("Sam").roles(Security.ORGANIZER_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "event")
				.param("description", "this is an test event")
				.param("venue.id", "1")
				.param("date", "2022-06-10")
				.param("time", "23:17")				
				.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isFound())
				.andExpect(view().name("redirect:/events")).andExpect(model().hasNoErrors())
				.andExpect(handler().methodName("createEvent")).andExpect(flash().attributeExists("ok_message"));

		verify(eventService).save(arg.capture());
		assertEquals("event", arg.getValue().getName());
	}

	@Test 
	public void deleteEventWithWrongAuthority() throws Exception {
		when(eventService.findById(25)).thenReturn(Optional.of(event));
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());
		when(event.getVenue()).thenReturn(venue);
		doNothing().when(eventService).deleteById(25);
		doNothing().when(venue).setEmpty(true);
	
		mvc.perform(delete("/events/25").with(user("attendee").roles(Security.ATTENDEE_ROLE))
		.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isForbidden());
		
		verify(eventService, never()).deleteById(25);
	}
	
	
	@Test 
	public void deleteEventThatDoesNotExist() throws Exception {
		doNothing().when(eventService).deleteById(any(Long.class));
		
		mvc.perform(delete("/events/99").with(user("Sam").roles(Security.ORGANIZER_ROLE))
		.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isNotFound())
		.andExpect(view().name("events/not_found")).andExpect(handler().methodName("deleteEvent"));
		
		verify(eventService, never()).deleteById(25);
	}
	
	@Test
	public void deleteEventSuccess() throws Exception {
		when(eventService.findById(25)).thenReturn(Optional.of(event));
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());
		when(event.getVenue()).thenReturn(venue);
		doNothing().when(eventService).deleteById(25);
		doNothing().when(venue).setEmpty(true);
	
		mvc.perform(delete("/events/25").with(user("Sam").roles(Security.ORGANIZER_ROLE))
		.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isFound())
		.andExpect(view().name("redirect:/events")).andExpect(handler().methodName("deleteEvent"));
		
		verify(eventService).findById(25);
		verify(eventService).deleteById(25);
	}
	
	
}
