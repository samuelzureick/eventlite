package uk.ac.man.cs.eventlite.entities;

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

	private boolean empty = true;

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean getEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
}
