package uk.ac.man.cs.eventlite.controllers;

import java.util.ArrayList;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.exceptions.EventNotFoundException;

@Controller
@RequestMapping(value = "", produces = { MediaType.TEXT_HTML_VALUE })
public class IndexController {
	
	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@ExceptionHandler(EventNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String eventNotFoundHandler(EventNotFoundException ex, Model model) {
		model.addAttribute("not_found_id", ex.getId());

		return "events/not_found";
	}
	
	@GetMapping
	public String getAllEvents(Model model) {
		Iterable<Event> events = eventService.findAll();
		ArrayList<Event> pastEvents = eventService.splitEventPast(events);
		ArrayList<Event> futureEvents = eventService.splitEventFuture(events);

		model.addAttribute("pastEvents", pastEvents);
		model.addAttribute("futureEvents", futureEvents);

		return "homepage/index";
	}
	
	
}