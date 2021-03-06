package org.fstn.test.model;


import java.util.List;

import javax.persistence.Entity;

import org.fstn.exportable.annotation.ExportField;
import org.fstn.exportable.model.Exportable;

// TODO: Auto-generated Javadoc
/**
 * The Class Customer.
 *
 * @author stephen
 */
@Entity
public class Customer implements Exportable{

	/** The last name. */
	@ExportField
	protected String lastName;
	
	/** The first name. */
	@ExportField
	protected String firstName;
	
	@ExportField
	protected List<Vehicule> vehicules;
	
	/**
	 * Gets the last name.
	 *
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	
	/**
	 * Sets the last name.
	 *
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	/**
	 * Gets the first name.
	 *
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	
	/**
	 * Sets the first name.
	 *
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Override
	public String exportColumnHeader() {
		return "Customer";
	}

	/**
	 * @return the vehicules
	 */
	public List<Vehicule> getVehicules() {
		return vehicules;
	}

	/**
	 * @param vehicules the vehicules to set
	 */
	public void setVehicules(List<Vehicule> vehicules) {
		this.vehicules = vehicules;
	}
	
	
}
