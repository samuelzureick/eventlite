package uk.ac.man.cs.eventlite.controllers;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
@WebMvcTest(VenuesController.class)
@Import(Security.class)
public class VenuesControllerTest {

	@Autowired
	private MockMvc mvc;

	@Mock
	private Venue venue;

	@Mock
	private Event event;

	@MockBean
	private EventService eventService;

	@MockBean
	private VenueService venueService;

	@Test
	public void getIndexWhenNoVenues() throws Exception {
		when(venueService.findAll()).thenReturn(Collections.<Venue>emptyList());

		mvc.perform(get("/venues").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("venues/index")).andExpect(handler().methodName("getAllVenues"));

		verify(venueService).findAll();
		verifyNoInteractions(venue);
	}

	@Test
	public void getIndexWithVenues() throws Exception {
		when(venueService.findAll()).thenReturn(Collections.<Venue>singletonList(venue));

		mvc.perform(get("/venues").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("venues/index")).andExpect(handler().methodName("getAllVenues"));

		verify(venueService).findAll();
	}

	@Test
	public void getVenueNotFound() throws Exception {
		mvc.perform(get("/venues/99").accept(MediaType.TEXT_HTML)).andExpect(status().isNotFound())
				.andExpect(view().name("venues/not_found")).andExpect(handler().methodName("getVenue"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getVenueFound() throws Exception {
		when(venueService.findById(25)).thenReturn(Optional.of(venue));
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());
		doNothing().when(venue).setEmpty(true);
		when(eventService.splitEventFuture(any(List.class))).thenReturn(Collections.<Event>emptyList());

		mvc.perform(get("/venues/25").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
			.andExpect(view().name("venues/details")).andExpect(handler().methodName("getVenue"))
			.andExpect(model().hasNoErrors()).andExpect(model().attribute("venue", venue));

		verify(venueService).findById(25);
		verify(eventService).findAll();
		verify(eventService).splitEventFuture(any(List.class));
	}

	@Test
	public void getNewVenuePage() throws Exception {
		mvc.perform(get("/venues/new").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
			.andExpect(view().name("venues/new")).andExpect(handler().methodName("newVenue"))
			.andExpect(model().hasNoErrors());
	}

	@Test
	public void createNewVenueSuccess() throws Exception {
		ArgumentCaptor<Venue> arg = ArgumentCaptor.forClass(Venue.class);
		doNothing().when(venueService).save(any(Venue.class));

		mvc.perform(post("/venues/new").with(user("Ryan").roles(Security.ORGANIZER_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "venue")
				.param("address", "23 Manchester Road E14 3BD")
				.param("capacity", "10")
				.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isFound())
				.andExpect(view().name("redirect:/venues")).andExpect(model().hasNoErrors())
				.andExpect(handler().methodName("createVenue")).andExpect(flash().attributeExists("ok_message"));

		verify(venueService).save(arg.capture());
		assertEquals("venue", arg.getValue().getName());
	}

	@Test
	public void createNewVenueWithWrongAuthority() throws Exception {
		ArgumentCaptor<Venue> arg = ArgumentCaptor.forClass(Venue.class);

		mvc.perform(post("/venues/new").with(user("attendee").roles(Security.ATTENDEE_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "venue")
				.param("address", "23 Manchester Road E14 3BD")
				.param("capacity", "10")
				.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isForbidden());

		verify(venueService, never()).save(arg.capture());
	}

	@Test
	public void deleteVenueSuccess() throws Exception {
		when(venueService.findById(25)).thenReturn(Optional.of(venue));
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());
		doNothing().when(venueService).deleteById(25);
	
		mvc.perform(delete("/venues/25").with(user("Ryan").roles(Security.ORGANIZER_ROLE))
			.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isFound())
			.andExpect(view().name("redirect:/venues")).andExpect(handler().methodName("deleteVenue"))
			.andExpect(flash().attributeExists("ok_message"));
		
		verify(venueService).findById(25);
		verify(venueService).deleteById(25);
	}

	@Test
	public void deleteVenueThatDoesNotExist() throws Exception {
		mvc.perform(delete("/venues/99").with(user("Ryan").roles(Security.ORGANIZER_ROLE))
			.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isNotFound())
			.andExpect(view().name("venues/not_found")).andExpect(handler().methodName("deleteVenue"));

		verify(venueService, never()).deleteById(99);
	}

	@Test
	public void deleteVenueThatHasEvent() throws Exception {
		when(venueService.findById(25)).thenReturn(Optional.of(venue));
		List<Event> events = new ArrayList<Event>();
		events.add(event);
		when(eventService.findAll()).thenReturn(events);
		when(event.getVenue()).thenReturn(venue);
		when(venue.getId()).thenReturn(25l);
	
		mvc.perform(delete("/venues/25").with(user("Ryan").roles(Security.ORGANIZER_ROLE))
			.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isFound())
			.andExpect(view().name("redirect:/venues/25")).andExpect(handler().methodName("deleteVenue"));
		
		verify(venueService).findById(25);
		verify(eventService).findAll();
		verify(event).getVenue();
		verify(venue).getId();
	}

	@Test
	public void deleteVenueWithWrongAuthority() throws Exception {
		mvc.perform(delete("/venues/25").with(user("attendee").roles(Security.ATTENDEE_ROLE))
				.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isForbidden());
		
		verify(eventService, never()).findById(25);
		verify(eventService, never()).deleteById(25);
	}

	@Test
	public void getVenueUpdatePage() throws Exception {
		when(venueService.findById(25)).thenReturn(Optional.of(venue));

		mvc.perform(get("/venues/update/25").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
			.andExpect(view().name("venues/update")).andExpect(handler().methodName("getVenueUpdate"))
			.andExpect(model().hasNoErrors());

		verify(venueService).findById(25);
	}

	@Test
	public void updateVenueSuccess() throws Exception {
		ArgumentCaptor<Venue> arg = ArgumentCaptor.forClass(Venue.class);
		when(venueService.findById(25)).thenReturn(Optional.of(venue));
		doNothing().when(venueService).save(any(Venue.class));

		mvc.perform(post("/venues/update").with(user("Ryan").roles(Security.ORGANIZER_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", "25")
				.param("name", "venue")
				.param("address", "23 Manchester Road E14 3BD")
				.param("capacity", "10")		
				.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isFound())
				.andExpect(view().name("redirect:/venues/25")).andExpect(model().hasNoErrors())
				.andExpect(handler().methodName("updateVenue")).andExpect(flash().attributeExists("ok_message"));

		verify(venueService).save(arg.capture());
	}

	@Test
	public void updateVenueWithErrors() throws Exception {
		ArgumentCaptor<Venue> arg = ArgumentCaptor.forClass(Venue.class);

		mvc.perform(post("/venues/update").with(user("Ryan").roles(Security.ORGANIZER_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", "")  // Errors because we must state which venue to update
				.param("name", "venue")
				.param("address", "23 Manchester Road E14 3BD")
				.param("capacity", "10")		
				.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
				.andExpect(view().name("venues/update")).andExpect(model().hasErrors())
				.andExpect(handler().methodName("updateVenue"));

		verify(venueService, never()).save(arg.capture());
	}

	@Test
	public void updateVenueWithWrongAuthority() throws Exception {
		ArgumentCaptor<Venue> arg = ArgumentCaptor.forClass(Venue.class);

		mvc.perform(post("/venues/update").with(user("attendee").roles(Security.ATTENDEE_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "venue")
				.param("address", "23 Manchester Road E14 3BD")
				.param("capacity", "10")
				.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isForbidden());

		verify(venueService, never()).save(arg.capture());
	}

	@Test
	public void searchVenue() throws Exception {
		when(venueService.listAll("search_venue")).thenReturn(Collections.<Venue>emptyList());

		mvc.perform(get("/venues/search")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("keyword", "search_venue")
				.accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("venues/index")).andExpect(model().hasNoErrors())
				.andExpect(handler().methodName("getSearchVenues"));

		verify(venueService).listAll("search_venue");
	}

}
