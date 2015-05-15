package de.unibamberg.eesys.projekt.businessobjects;

import java.util.List;

import android.location.Location;
import de.unibamberg.eesys.projekt.L;

/**
 * The ChargingStation represents a specific charging station
 * and defines the charging and location related values of the
 * charging station.
 * 
 * @author Stefan
 * @version 1.0
 *
 */
public class ChargingStation {
	long id;
	ChargingType chargingType;
	GeoCoordinate geoCoordinate;
	String name;
	String description;
	private final static double MIN_DISTANCE_TO_CHARGESTATION = 30;

	private double chargedEnergy; // in kWh

	/**
	 * Calculates the charged energy for a given duration.
	 * 
	 * @param duration in hours (double)
	 * @return charged energy in kWh
	 */
	public double charge(double duration) {
		chargedEnergy = (chargingType.getChargingPower() * (duration));
		return chargedEnergy;
	}

	/**
	 * Returns the (current) charged energy.
	 * 
	 * @return charged enery in kWh
	 */
	public double getCharge() {
		return chargedEnergy;
	}

	/**
	 * Returns the unique ID of this charging station.
	 * 
	 * @return Id (long)
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the unique ID of this charging station.
	 * @param id (long)
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Returns the charging type of this charging station.
	 * 
	 * @return charging type (business object)
	 */
	public ChargingType getChargingType() {
		return chargingType;
	}

	/**
	 * Sets the charging type of this charging station.
	 * 
	 * @param chargingType (business object)
	 */
	public void setChargingType(ChargingType chargingType) {
		this.chargingType = chargingType;
	}

	/**
	 * Returns the location (geo coordinate) of this charging station.
	 * 
	 * @return GeoCoordinate (business object)
	 */
	public GeoCoordinate getGeoCoordinate() {
		return geoCoordinate;
	}

	/**
	 * Sets the location (geo coordinate) of this charging station.
	 * 
	 * @param geoCoordinate (business object)
	 */
	public void setGeoCoordinate(GeoCoordinate geoCoordinate) {
		this.geoCoordinate = geoCoordinate;
	}

	/**
	 * Returns the name of this charging station.
	 * 
	 * @return name (String)
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this charging station.
	 * 
	 * @param name (String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the description of this charging station.
	 * 
	 * @return description (String)
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of this charging station
	 * 
	 * @param description (String)
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the geo coordinate, name and description of this charging station.
	 * 
	 * @return "geoCoordinate | name | description" (String)
	 * 
	 */
	public String toString() {
		return geoCoordinate.toString() + " | " + name + " | " + description;
	}

	/**
	 * Checks if there is a charging station that is close to the provided WayPoint.
	 * If the charging station is not farther away than the minDistance (constant)
	 * it will be returned.
	 * 
	 * @param w (WayPoint)
	 * @param chargingStations (List of ChargingStations)
	 * @return a (near) charging charging station
	 *         or null if there is no charging station close enough 
	 */
	public static ChargingStation getNearestChargeStation(WayPoint w,
			List<ChargingStation> chargingStations) {
		ChargingStation result = null;

		double minDistance = MIN_DISTANCE_TO_CHARGESTATION; // distance that a
															// charge station
															// has to be
		for (ChargingStation c : chargingStations) {
//			L.v(c.getGeoCoordinate().toString());
			float[] distance = new float[3];
			Location.distanceBetween(w.getGeoCoordinate().getLatitude(), w
					.getGeoCoordinate().getLongitude(), c.getGeoCoordinate()
					.getLatitude(), c.getGeoCoordinate().getLongitude(),
					distance);
//			L.v("distance from charge station: " + distance[0]);
			if (distance[0] <= minDistance) {
				return c;
				// will stop if a charge station is found that is < e.g 15
				// meters (minDistance) away
				// will not go on to find a closer charge station (!)
			}
		}
		return result; // will return null if no charge station was closer than
						// min distance
	}
}
