package de.unibamberg.eesys.projekt;

import android.preference.PreferenceManager;

/** 
 * contains parameters used for fine tuning app behavior and overriding certain aspects while debugging
 * @author Pascal
 *
 */
public class Params {
	
	private AppContext appContext; 
	private static final String PREF_TESTING_HEIGHT_DELTA = "testing.height";

	// Dashboard 
	public static final int THRESSHOLD_ACCELERATION_FOR_GREEN_BACKGROUND = 20; // if current consumption is > 200 kWh, background should be red 
	public static final int NUMBER_OF_CHARGE_STATIONS_TO_SHOW = 3;
	// MobilityManager
		//Verzögerung die gewartet wird, bis die neue Activity umgestellt wird
		// currently only every 10 minutes (de facto disabled), before: 1s 
		public static final long ACTIVITY_RECOGNITION_UPDATE_TIME = 600*1000;		 
	//	private static final long ACTIVITY_RECOGNITION_UPDATE_TIME = 360000000;
		// idea: use dynamic interval depending on speed of last waypoint
		public static final long GPS_UPDATE_TIME = 1*1000;
		// Ecar
		public static final int MIN_SPEED_FOR_MOVING = 3; // 3m/s = 10.8 km/h	
		/**
			 * duration in seconds that a car may be still, before trip ends used to
			 * prevent short stops (e.g. traffic lights) to end trip "Ampeltimeout"
			 */
		public static double MAX_VEHICLE_STILL_DURATION = 3; // 3 seconds
//		public static double MAX_VEHICLE_STILL_DURATION = 60 * 2; // 2 minutes	


public Params(AppContext appContext) {
	this.appContext = appContext;
}


public boolean isFakeHeightSet() {
	String testingHeight = PreferenceManager.getDefaultSharedPreferences(
			appContext).getString(PREF_TESTING_HEIGHT_DELTA, "disabled");
	if (testingHeight.equals("disabled"))
		return false;
		else return true;
}

//... height difference - override data with debug values specified in preferences
public double getFakeHeight() {
	double altitudeDiffInMeters = 0; 
	
	// ... height difference - override data with debug values specified in preferences
	String testingHeight = PreferenceManager.getDefaultSharedPreferences(
			appContext).getString(PREF_TESTING_HEIGHT_DELTA, "disabled");
	if (!testingHeight.equals("disabled")) {
		try {
			altitudeDiffInMeters = Float.parseFloat(testingHeight);
		}
		catch (NumberFormatException e) {
			L.e("Testing height: invalid value.");
			e.printStackTrace();
		}
	}
	return altitudeDiffInMeters;
	}

}

