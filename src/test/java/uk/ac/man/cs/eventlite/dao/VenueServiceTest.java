package uk.ac.man.cs.eventlite.dao;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
import uk.ac.man.cs.eventlite.entities.Venue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EventLite.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class VenueServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private VenueService venueService;

	private Venue venue = new Venue();

	@BeforeEach
	public void initialise() {
        venue.setName("Temporary Venue");
        venue.setAddress("Stuart Road WA15 8QY");
        venue.setCapacity(1250);
	}

	@Test
	public void countVenueTest() {
		assertEquals(3, venueService.count());
	}

	@Test
	public void findAllVenuesTest() {
		List<Venue> venuesList = new ArrayList<Venue>();
		Iterable<Venue> venuesUT = venueService.findAll();
		for (Venue e : venuesUT) {
			venuesList.add(e);
		}
		assertEquals(3, venuesList.size());
		assertEquals("Venue A", venuesList.get(0).getName());
		assertEquals("Venue B", venuesList.get(1).getName());
		assertEquals("Venue C", venuesList.get(2).getName());
	}

	@Test
	public void saveVenueTest() {
		venueService.save(venue);
		assertEquals(4, venueService.count());
		Venue testVenue = venueService.findById(venue.getId()).orElse(null);
		assertNotNull(testVenue);
		assertEquals(venue.getName(), testVenue.getName());
		assertEquals(venue.getAddress(), testVenue.getAddress());
		assertEquals(venue.getCapacity(), testVenue.getCapacity());
	}

	@Test
	public void deleteByIdTest() {
		venueService.save(venue);
		venueService.deleteById(venue.getId());
		assertFalse(venueService.findById(venue.getId()).isPresent());
	}

	@Test
	public void findByIdTest() {
		venueService.save(venue);
		Venue testVenue = venueService.findById(venue.getId()).orElse(null);
		assertNotNull(testVenue);
		assertEquals(venue.getName(), testVenue.getName());
	}
}
