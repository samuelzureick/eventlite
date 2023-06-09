package uk.ac.man.cs.eventlite.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.exceptions.VenueNotFoundException;

@Controller
@RequestMapping(value = "/venues", produces = { MediaType.TEXT_HTML_VALUE })
public class VenuesController {

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@ExceptionHandler(VenueNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String venueNotFoundHandler(VenueNotFoundException ex, Model model) {
		model.addAttribute("not_found_id", ex.getId());
		return "venues/not_found";
	}

	@GetMapping("/{id}")
	public String getVenue(@PathVariable("id") long id, Model model) {
		Venue venue = venueService.findById(id).orElseThrow(() -> new VenueNotFoundException(id));

		boolean venueEmpty = true;
		List<Event> events = new ArrayList<Event>();
		for (Event event : eventService.findAll()) {
			if (event.getVenue() == venue) {
				venueEmpty = false;
				events.add(event);
			}
		}
		venue.setEmpty(venueEmpty);
		events = eventService.splitEventFuture(events);

		model.addAttribute("venue", venue);
		model.addAttribute("events", events);

		return "venues/details";
	}

	@GetMapping
	public String getAllVenues(Model model) {
		Iterable<Venue> venues = venueService.findAll();
		model.addAttribute("venues", venues);
		return "venues/index";
	}

	@RequestMapping("/search")
	public String getSearchVenues(Model model, @RequestParam String keyword) {
		Iterable<Venue> listSearchVenues = venueService.listAll(keyword);
		model.addAttribute("venues", listSearchVenues);
		return "venues/search";
	}

	@GetMapping("/new")
	public String newVenue(Model model) {
		if (!model.containsAttribute("venue")) {
			model.addAttribute("venue", new Venue());
		}

		return "venues/new";
	}

	@PostMapping("/new")
	public String createVenue(@Valid @ModelAttribute Venue venue, BindingResult errors,
			Model model, RedirectAttributes redirectAttrs) {

		if (errors.hasErrors()) {
			System.out.println(errors);
			model.addAttribute("venue", venue);
			return "venues/new";
		}

		venueService.save(venue);
		redirectAttrs.addFlashAttribute("ok_message", "New venue added.");

		return "redirect:/venues/";
	}
	
	@DeleteMapping(value = "/{id}")
	public String deleteVenue(@PathVariable("id") long id, Model model, RedirectAttributes redirectAttrs) {
		Venue venue = venueService.findById(id).orElseThrow(() -> new VenueNotFoundException(id));
		Iterable<Event> events = eventService.findAll();
		boolean venueEmpty = true;
		for (Event event : events) {
			if (event.getVenue() == venue) {
				venueEmpty = false;
			}
		}
		if (venueEmpty) {
			venueService.deleteById(id);
			redirectAttrs.addFlashAttribute("ok_message", "Venue deleted.");
			return "redirect:/venues/";
		}

		return "redirect:/venues/" + venue.getId() + "/";
	}
	
	@GetMapping("/update/{id}")
	public String getVenueUpdate(@PathVariable("id") long id, Model model) {
		Venue venue = venueService.findById(id).orElseThrow(() -> new VenueNotFoundException(id));

		model.addAttribute("venue", venue);

		return "venues/update";
	}
	
	@PostMapping("/update")
	public String updateVenue(@Valid @ModelAttribute Venue venue, BindingResult errors,
			Model model, RedirectAttributes redirectAttrs) {

		if (errors.hasErrors()) {
			model.addAttribute("venue", venue);
			return "venues/update";
		}

		venueService.save(venue);

		redirectAttrs.addFlashAttribute("ok_message", "Venue updated.");
		return "redirect:/venues/" + venue.getId() + "/";
	}

}
