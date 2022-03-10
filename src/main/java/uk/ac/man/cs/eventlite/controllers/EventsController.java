package uk.ac.man.cs.eventlite.controllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.exceptions.EventNotFoundException;

@Controller
@RequestMapping(value = "/events", produces = { MediaType.TEXT_HTML_VALUE })
public class EventsController {

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

	@GetMapping("/{id}")
	public String getEvent(@PathVariable("id") long id, Model model) {
		Event event = eventService.findById(id).orElseThrow(() -> new EventNotFoundException(id));

		model.addAttribute("event", event);

		return "events/details";
	}
	
	@GetMapping("/update/{id}")
	public String getEventUpdate(@PathVariable("id") long id, Model model) {
		Event event = eventService.findById(id).orElseThrow(() -> new EventNotFoundException(id));

		model.addAttribute("event", event);
		model.addAttribute("venues", venueService.findAll());

		return "events/update";
	}
	
	@PostMapping("/update")
	public String updateEvent(@ModelAttribute Event event, BindingResult errors,
			Model model, RedirectAttributes redirectAttrs) {

		if (errors.hasErrors()) {
			model.addAttribute("event", event);
			return "/events/update";
		}

		eventService.save(event);
		redirectAttrs.addFlashAttribute("ok_message", "Event updated.");

		return "/events/details";
	}
	
	@GetMapping("/new")
	public String newEvent(Model model) {
		if (!model.containsAttribute("event")) {
			model.addAttribute("event", new Event());
		}
		
		model.addAttribute("venues", venueService.findAll());

		return "events/new";
	}
	
	@PostMapping("/new")
	public String createEvent(@ModelAttribute Event event, BindingResult errors,
			Model model, RedirectAttributes redirectAttrs) {

		if (errors.hasErrors()) {
			model.addAttribute("event", event);
			model.addAttribute("venues", venueService.findAll());
			return "events/new";
		}

		eventService.save(event);
		redirectAttrs.addFlashAttribute("ok_message", "New event added.");

		return "redirect:/events";
	}

	@GetMapping
	public String getAllEvents(Model model) {
		model.addAttribute("events", eventService.findAll());

		return "events/index";
	}
	
	@RequestMapping("/search")
	public String getSearchEvents(Model model, @RequestParam String keyword) {
		Iterable<Event> listSearchEvents = eventService.listAll(keyword);
		ArrayList<Event> pastEvents = eventService.splitEventPast(listSearchEvents);
		ArrayList<Event> futureEvents = eventService.splitEventFuture(listSearchEvents);
		model.addAttribute("pastEvents", pastEvents);
		model.addAttribute("futureEvents", futureEvents);
		return "events/index";
	}
	
	@DeleteMapping(value = "/{id}")
	public String deleteEvent(@PathVariable("id") long id) {
		
		eventService.deleteById(id);
		
		return "redirect:/events";
	}
}
