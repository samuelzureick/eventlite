package uk.ac.man.cs.eventlite.dao;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

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

	@Test
	public void countVenueTest() {
		assertEquals(3, venueService.count());
	}

	@Test
	public void findAllVenuesTest() {
		ArrayList<Venue> venuesList = new ArrayList<Venue>();
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
		Venue venue = new Venue();
		venue.setName("Venue For Test");
        venue.setAddress("Stuart Road WA15 8QY");
        venue.setCapacity(1250);
        venueService.save(venue);
        assertEquals(4, venueService.count());
        Venue venueUT = venueService.findById(7).orElse(null);
        assertNotNull(venueUT);
        assertEquals("Venue For Test", venueUT.getName());
	}
}
