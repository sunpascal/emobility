package de.unibamberg.eesys.projekt.businessobjects;

/**
 * This class represents the different charging types of
 * a charging station (e. g. home charging, public charging). 
 * 
 * @author Stefan
 * @version 1.0
 * 
 */
public class ChargingType {
	private long id;
	private String name;
	private String description;
	private double chargingPower;
	
	/**
	 * empty Constructor of ChargingType
	 */
	public ChargingType() {
	}
	/**
	 * Constructor of ChargingType 
	 * @param id
	 * @param name
	 * @param description
	 * @param chargingPower
	 */
	public ChargingType(int id, String name, String description,
			double chargingPower) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.chargingPower = chargingPower;
	}
	/**
	 * gets ID of ChargingType
	 * @return id
	 */
	public long getId() {
		return id;
	}
	/**
	 * sets ID of ChargingType
	 * @param id to be set 
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * gets name of ChargingType
	 * @return name
	 */
	public String getName() {
		return name;
	}
	/**
	 * sets name of chargingType
	 * @param name to be set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * gets Description of chargingType
	 * @return description to set
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * sets description of chargingType
	 * @param description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * gets charging Power of charging Station
	 * @return power
	 */
	public double getChargingPower() {
		return chargingPower;
	}
	/**
	 * sets charging power of charging station
	 * @param chargingPower to set in kWh
	 */
	public void setChargingPower(double chargingPower) {
		this.chargingPower = chargingPower;
	}

	@Override
	public String toString() {
		return name;
	}

}
