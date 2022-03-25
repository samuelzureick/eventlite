package uk.ac.man.cs.eventlite.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.geojson.Point; 
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;

@Entity
@Table(name = "venues")
public class Venue {

	@Id
	@GeneratedValue
	private long id;

	@NotEmpty(message = "Name cannot be empty.")
	@Size(max = 256, message = "The name must have 256 characters or less.")
	@Column
	private String name;

	@NotNull
	@Min(value=1, message = "Capacity must be an integer greater or equal to 1.")
	@Column
	private int capacity;
	
	@NotEmpty(message = "Address cannot be empty.")
	@Size(max = 300, message = "The address must have 300 characters or less.")
	@Pattern(regexp=".*(Street|Avenue|Road|Rd|St|Ave)\\s[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][ABD-HJLNP-UW-Z]{2}$")
	@Column
	private String address;
	
	@Column
	private double longitude;
	
	@Column
	private double latitude;

	public Venue() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public double getLongitude() {
		System.out.println(longitude);
		return longitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLongitude(double lng) {
		System.out.println(lng);
		this.longitude= lng;
	}
	
	public void setLatitude(double lat) {
		this.latitude= lat;
		System.out.println(this.latitude);
	}	
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
		
		MapboxGeocoding client = MapboxGeocoding.builder()
				.accessToken("pk.eyJ1IjoidGVhbWcxMCIsImEiOiJjbDE1ODIyZngwMG92M2pxczVkajF5YWQ4In0.JAscVbHj6h0TpFGWK4YU_A")
				.query(address).build();
		
		class GeoResponse implements Callback<GeocodingResponse> {

			public Point resultPoint;

			@Override
			public void onResponse(Call <GeocodingResponse> call, Response <GeocodingResponse> response) {
				List<CarmenFeature> results = response.body().features();
				if (results.size() >0) {
					resultPoint = results.get(0).center();
				}
				else {
				}
			}
			
			@Override
			public void onFailure(Call<GeocodingResponse> call, Throwable t) {
				System.out.println(t);			
			}
		}
		
		GeoResponse geoResponse = new GeoResponse();
		
		client.enqueueCall(geoResponse);
		
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setLongitude(geoResponse.resultPoint.longitude());
		setLatitude(geoResponse.resultPoint.latitude());
	}
}
