package de.unibamberg.eesys.projekt.businessobjects;

/**
 * A instance of this class represents the used battery
 * for the current eCars.
 * (or in a further step for different eCar instances)
 * 
 * @author Stefan
 * @version 1.0
 *
 */
public class Battery {
	long id;
	double currentSoc; // in kWh, not percent!
	boolean charging;

	/**
	 * Unique ID of the battery instance.
	 * 
	 * @return id (long)
	 */
	public long getId() {
		return id;
	}

	/**
	 * Set the unique ID of the battery instance.
	 * Usually only used to restore objects (load from DB).
	 * 
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Get the current state of charge of this battery
	 * 
	 * @return currentSoc (double)
	 */
	public double getCurrentSoc() {
		return currentSoc;
	}

	/**
	 * Set the current state of charge of this battery 
	 * 
	 * @param currentSoc (double)
	 */
	public void setCurrentSoc(double currentSoc) {
		this.currentSoc = currentSoc;
	}

	/**
	 * Get the current charging state "isCharging"
	 * 
	 * @return charging (boolean)
	 */
	public boolean isCharging() {
		return charging;
	}

	/**
	 * Set the current charging state to true or false
	 * 
	 * @param charging
	 */
	public void setCharging(boolean charging) {
		this.charging = charging;
	}

	/**
	 * Constructor
	 */
	public Battery() {
		// initialize default values
		charging = false;

		// chargingStation = new ChargingStation();
		// ChargingType homeCharging = new ChargingType(1, "Home Charging",
		// "description", 240, 10);
		// ChargingType publicCharging = new ChargingType(2, "Public Charging",
		// "description", 240, 32);
	}
}
