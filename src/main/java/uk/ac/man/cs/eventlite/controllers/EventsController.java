package uk.ac.man.cs.eventlite.controllers;

import javax.validation.Valid;
import java.util.List;

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

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.TwitterService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.exceptions.EventNotFoundException;

@Controller
@RequestMapping(value = "/events", produces = { MediaType.TEXT_HTML_VALUE })
public class EventsController {

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;
	
	private Twitter twitter = new TwitterService().getTwitter();

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
	public String updateEvent(@Valid @ModelAttribute Event event, BindingResult errors,
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
	public String createEvent(@Valid @ModelAttribute Event event, BindingResult errors,
			Model model, RedirectAttributes redirectAttrs) {

		if (errors.hasErrors()) {
			System.out.println(errors);
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
		Iterable<Event> events = eventService.findAll();
		List<Event> pastEvents = eventService.splitEventPast(events);
		List<Event> futureEvents = eventService.splitEventFuture(events);
		
		try {
			ResponseList<Status> tweets = twitter.getUserTimeline();
			model.addAttribute("tweets", tweets.subList(0, Math.min(5, tweets.size())));
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		model.addAttribute("pastEvents", pastEvents);
		model.addAttribute("futureEvents", futureEvents);
		model.addAttribute("venues", venueService.findAll());

		return "events/index";
	}

	@RequestMapping("/search")
	public String getSearchEvents(Model model, @RequestParam String keyword) {
		Iterable<Event> listSearchEvents = eventService.listAll(keyword);
		List<Event> pastEvents = eventService.splitEventPast(listSearchEvents);
		List<Event> futureEvents = eventService.splitEventFuture(listSearchEvents);
		model.addAttribute("pastEvents", pastEvents);
		model.addAttribute("futureEvents", futureEvents);
		return "events/index";
	}

	@DeleteMapping(value = "/{id}")
	public String deleteEvent(@PathVariable("id") long id) {
		Event event = eventService.findById(id).orElseThrow(() -> new EventNotFoundException(id));
		Venue venue = event.getVenue();

		eventService.deleteById(id);
		Iterable<Event> events = eventService.findAll();
		boolean venueEmpty = true;
		for (Event venue_event : events) {
			if (venue_event.getVenue() == venue) {
				venueEmpty = false;
			}
		}
		venue.setEmpty(venueEmpty);
		return "redirect:/events";
	}

}
