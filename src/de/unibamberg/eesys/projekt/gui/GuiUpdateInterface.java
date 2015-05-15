/**
 * @author Robert Lang, Pascal Lüders
 */

package de.unibamberg.eesys.projekt.gui;

import de.unibamberg.eesys.projekt.businessobjects.Ecar.CarState;
import de.unibamberg.eesys.projekt.businessobjects.WayPoint;

/**
 * The interface is implemented by the activity which displays the real-time reports. 
 * It provides the needed callbacks.
 */
public interface GuiUpdateInterface {
	/**
	 * onGpsUpdate called when a new WayPoint is detected 
	 * @param data = a new Waypoint detected by the GPS 
	 */
	public void onGpsUpdate(WayPoint data);
	
	/**
	 * onGpsDisabled called when the user disables the GPS while App is running 
	 */
	public void onGpsDisabled();
	
	/**
	 * onGpsDisabled called when the user disables the GPS while App is running 
	 */
	public void onGpsEnabled();

	/**
	 * called when GPS Signal changes
	 * @param status of GPS Provider 
	 * 			0 = OUT_OF_SERVICE
	 * 			1 = TEMPORARILY_UNAVAILABLE
	 * 			2 = AVAILABLE
	 */
	void onStatusChanged(int status);
	
	/**
	 * onAkkuUpdate called when a new battery level is reached
	 * @param akkuValue = new battery value to display in main overview
	 */
	public void onAkkuUpdate(String akkuValue);
	
	/**
	 * onSpeedUpdate called when a new speed value has been calculated based on GPS signals
	 * @param speed = new speed value to display in main overview
	 */
	public void onSpeedUpdate(String speed);
	
	/**
	 * onConsumptionUpdate called when a new consumption values has been calculated
	 * @param consumedEnergy = value to be displayed in main overview
	 */
	public void onConsumptionUpdate(String consumedEnergy);
	
	/**
	 * onRemainingTimeOnBatteryUpdate called when the battery level is decreasing while driving and a new battery level has been reached
	 * @param timeOnBattery = value to be displayed in main overview
	 */
	public void onRemainingTimeOnBatteryUpdate(String timeOnBattery);
	
	/**
	 * onCharchingTimeRemainingUpdate called when the charging level is rising and a new value has been calculated for the time until 100% are reached
	 * @param time = value to be displayed in main overview
	 */
	public void onCharchingTimeRemainingUpdate(String time);
	
	/**
	 * onCoveredDistanceUpdate called when a new distance has been calculated while driving 
	 * @param coveredDistance = value to be displayed in main overview
	 */
	public void onCoveredDistanceUpdate(String coveredDistance);

	/**
	 * onCarStateUpdate called when car state is changing (driving, parking, charging)
	 * @param carState = value to be displayed in main overview
	 */
	public void onCarStateUpdate(CarState carState);

}
