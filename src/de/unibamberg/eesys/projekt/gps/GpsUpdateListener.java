package de.unibamberg.eesys.projekt.gps;

import de.unibamberg.eesys.projekt.businessobjects.WayPoint;

/**
 * Interface defining how data is exchanged between app background and GUI. 
 * 
 * @author pascal
 *
 */
public interface GpsUpdateListener {

	/** 
	 * called when there is a new GPS sensor or play service status update.
	 * WayPoint object is passed to GUI which can use the contained data to 
	 * update the GUI. 
	 * @param data
	 */
	void onWayPointUpdate(WayPoint data);
	
	/** 
	 * called when a user disables GPS in the Android menu.
	 */
	void onGpsDisabled();
	
	/** 
	 * called when a user enables GPS in the Android menu.
	 */
	void onGpsEnabled();
	
	/**
	 * called when GPS Signal changes
	 * @param status of GPS Provider 
	 * 			0 = OUT_OF_SERVICE
	 * 			1 = TEMPORARILY_UNAVAILABLE
	 * 			2 = AVAILABLE
	 */
	void onStatusChanged(int status);

}
