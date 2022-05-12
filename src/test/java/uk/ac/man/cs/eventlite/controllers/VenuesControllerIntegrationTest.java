package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import uk.ac.man.cs.eventlite.EventLite;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EventLite.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class VenuesControllerIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

	private static Pattern CSRF = Pattern.compile("(?s).*name=\"_csrf\".*?value=\"([^\"]+).*");
	private static String SESSION_KEY = "JSESSIONID";

	@LocalServerPort
	private int port;

	private WebTestClient client;

	@BeforeEach
	public void setup() {
		client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
	}

	@Test
	public void testGetAllVenues() {
		client.get().uri("/venues").accept(MediaType.TEXT_HTML).exchange().expectStatus().isOk().expectHeader()
				.contentTypeCompatibleWith(MediaType.TEXT_HTML).expectBody(String.class).consumeWith(result -> {
					assertThat(result.getResponseBody(), containsString("All Venues"));
				});
	}

	@Test
	public void getVenueNotFound() {
		client.get().uri("/venues/99").accept(MediaType.TEXT_HTML).exchange().expectStatus().isNotFound().expectHeader()
				.contentTypeCompatibleWith(MediaType.TEXT_HTML).expectBody(String.class).consumeWith(result -> {
					assertThat(result.getResponseBody(), containsString("Venue not found"));
					assertThat(result.getResponseBody(), containsString("99"));
				});
	}

	@Test
	public void getVenueFound() {
		client.get().uri("/venues/1").accept(MediaType.TEXT_HTML).exchange().expectStatus().isOk().expectHeader()
				.contentTypeCompatibleWith(MediaType.TEXT_HTML).expectBody(String.class).consumeWith(result -> {
					assertThat(result.getResponseBody(), containsString("Venue Detail"));
					assertThat(result.getResponseBody(), containsString("Venue B"));
				});
	}

	@Test
	public void getNewVenuePage() {
		client.get().uri("/venues/new").accept(MediaType.TEXT_HTML).exchange().expectStatus().isOk().expectHeader()
				.contentTypeCompatibleWith(MediaType.TEXT_HTML).expectBody(String.class).consumeWith(result -> {
					assertThat(result.getResponseBody(), containsString("Add a new venue"));
				});
	}

	@Test
	public void createNewVenue() {
		String[] tokens = login();

		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("_csrf", tokens[0]);
		form.add("name", "venue");
		form.add("address", "23 Manchester Road E14 3BD");
		form.add("capacity", "10");

		client.post().uri("/venues/new").accept(MediaType.TEXT_HTML).contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue(form).cookies(cookies -> {
					cookies.add(SESSION_KEY, tokens[1]);
				}).exchange().expectStatus().isFound().expectHeader().value("Location", endsWith("/venues/"));
	}

	@Test
	public void createNewVenueWithNoData() {
		String[] tokens = login();

		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("_csrf", tokens[0]);

		client.post().uri("/venues/new").accept(MediaType.TEXT_HTML).contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue(form).cookies(cookies -> {
					cookies.add(SESSION_KEY, tokens[1]);
				}).exchange().expectStatus().isOk().expectHeader()
				.contentTypeCompatibleWith(MediaType.TEXT_HTML).expectBody(String.class).consumeWith(result -> {
					assertThat(result.getResponseBody(), containsString("Add a new venue"));
				});
	}

	@Test
	public void createNewVenueWithBadData() {
		String[] tokens = login();

		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("_csrf", tokens[0]);
		form.add("name", "");
		form.add("address", "an invalid address");
		form.add("capacity", "-1");

		client.post().uri("/venues/new").accept(MediaType.TEXT_HTML).contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue(form).cookies(cookies -> {
					cookies.add(SESSION_KEY, tokens[1]);
				}).exchange().expectStatus().isOk().expectHeader()
				.contentTypeCompatibleWith(MediaType.TEXT_HTML).expectBody(String.class).consumeWith(result -> {
					assertThat(result.getResponseBody(), containsString("Add a new venue"));
				});
	}

	@Test
	public void createNewVenueUnauthorised() {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("name", "venue");
		form.add("address", "23 Manchester Road E14 3BD");
		form.add("capacity", "10");

		client.post().uri("/venues/new").accept(MediaType.TEXT_HTML).contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue(form).exchange().expectStatus().isFound().expectHeader().value("Location", endsWith("/sign-in"));
	}

	@Test
	public void deleteVenue() {
		String[] tokens = login();

		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("_csrf", tokens[0]);
		form.add("_method", "delete");

		client.post().uri("/venues/2").accept(MediaType.TEXT_HTML).contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue(form).cookies(cookies -> {
					cookies.add(SESSION_KEY, tokens[1]);
				}).exchange().expectStatus().isFound().expectHeader().value("Location", endsWith("/venues/"));
	}

	@Test
	public void deleteVenueNotExist() {
		String[] tokens = login();

		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("_csrf", tokens[0]);
		form.add("_method", "delete");

		client.post().uri("/venues/99").accept(MediaType.TEXT_HTML).contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue(form).cookies(cookies -> {
					cookies.add(SESSION_KEY, tokens[1]);
				}).exchange().expectStatus().isNotFound();
	}

	@Test
	public void deleteVenueUnauthorised() {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("_method", "delete");

		client.post().uri("/venues/2").accept(MediaType.TEXT_HTML).contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue(form).exchange().expectStatus().isFound().expectHeader().value("Location", endsWith("/sign-in"));
	}

	@Test
	public void getVenueUpdatePage() {
		client.get().uri("/venues/update/3").accept(MediaType.TEXT_HTML).exchange().expectStatus().isOk().expectHeader()
				.contentTypeCompatibleWith(MediaType.TEXT_HTML).expectBody(String.class).consumeWith(result -> {
					assertThat(result.getResponseBody(), containsString("Update Venue"));
					assertThat(result.getResponseBody(), containsString("Venue A"));
				});
	}

	@Test
	public void updateVenue() {
		String[] tokens = login();

		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("_csrf", tokens[0]);
		form.add("id", "3");
		form.add("name", "venue updated");
		form.add("address", "23 Manchester Road E14 3BD");
		form.add("capacity", "10");

		client.post().uri("/venues/update").accept(MediaType.TEXT_HTML).contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue(form).cookies(cookies -> {
					cookies.add(SESSION_KEY, tokens[1]);
				}).exchange().expectStatus().isFound().expectHeader().value("Location", endsWith("/venues/3/"));
	}

	@Test
	public void updateVenueWithBadData() {
		String[] tokens = login();

		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("_csrf", tokens[0]);
		form.add("id", "99");
		form.add("name", "");
		form.add("address", "an invalid address");
		form.add("capacity", "-1");

		client.post().uri("/venues/update").accept(MediaType.TEXT_HTML).contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue(form).cookies(cookies -> {
					cookies.add(SESSION_KEY, tokens[1]);
				}).exchange().expectStatus().isOk().expectHeader()
				.contentTypeCompatibleWith(MediaType.TEXT_HTML).expectBody(String.class).consumeWith(result -> {
					assertThat(result.getResponseBody(), containsString("Update Venue"));
				});
	}

	@Test
	public void updateVenueUnauthorised() {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("id", "3");
		form.add("name", "venue updated");
		form.add("address", "23 Manchester Road E14 3BD");
		form.add("capacity", "10");

		client.post().uri("/venues/update").accept(MediaType.TEXT_HTML).contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue(form).exchange().expectStatus().isFound().expectHeader().value("Location", endsWith("/sign-in"));
	}

	@Test
	public void searchVenue() {
		client.get().uri("/venues/search/?keyword=venue").accept(MediaType.TEXT_HTML).exchange().expectStatus().isOk().expectHeader()
				.contentTypeCompatibleWith(MediaType.TEXT_HTML).expectBody(String.class).consumeWith(result -> {
					assertThat(result.getResponseBody(), containsString("All Venues"));
				});
	}

	private String[] login() {
		String[] tokens = new String[2];

		EntityExchangeResult<String> result = client.mutate().filter(basicAuthentication("Ryan", "software")).build().get()
				.uri("/events").accept(MediaType.TEXT_HTML).exchange().expectBody(String.class).returnResult();
		tokens[0] = getCsrfToken(result.getResponseBody());
		tokens[1] = result.getResponseCookies().getFirst(SESSION_KEY).getValue();

		return tokens;
	}

	private String getCsrfToken(String body) {
		Matcher matcher = CSRF.matcher(body);

		assertThat(matcher.matches(), equalTo(true));

		return matcher.group(1);
	}

}
