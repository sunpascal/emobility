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
	
	// if current consumption is > 20 kWh, background will be red
	public static final int THRESSHOLD_ACCELERATION_FOR_GREEN_BACKGROUND = 20; 

	// up to 30 kwH background will be orange if > 30, background will be red
	public static final double THRESSHOLD_ACCELERATION_FOR_ORANGE_BACKGROUND = 30;		
		
	public static final int NUMBER_OF_CHARGE_STATIONS_TO_SHOW = 3;
	
	// MobilityManager
		//Verzögerung die gewartet wird, bis die neue Activity umgestellt wird
	
	
		// time out for activity recoginition - idea: use dynamic interval depending on speed of last waypoint 

	// every 10 minutes (de facto disabled), before: 1s - may be the cause why trips are not ended properly 
//		public static final long ACTIVITY_RECOGNITION_UPDATE_TIME = 600*1000;
		public static final long ACTIVITY_RECOGNITION_UPDATE_TIME = 1*1000;
				
		public static final long GPS_UPDATE_TIME = 1*1000;
		
		/**
		 * minimum distance between two points for a GPS point to be counter
		 */
		public static final long UPDATE_DISTANCE = 0;		// use 0 in order to detect when car has stopped		
		
		// Ecar
		public static final int MIN_SPEED_FOR_MOVING = 3; // 3m/s = 10.8 km/h	
		/**
			 * duration in seconds that a car may be still, before trip ends used to
			 * prevent short stops (e.g. traffic lights) to end trip "Ampeltimeout"
			 */
		public static String MAX_VEHICLE_STILL_DURATION = "119"; // 2 minutes	
		public static String PREF_MAX_VEHICLE_STILL_DURATION = "testing.maxvehiclestillduration";
		
		public String maxVehicleStillDuration = MAX_VEHICLE_STILL_DURATION;

		public static int NUMBER_OF_WAYPOINTS_UNTIL_NEW_ROADTYPE = 0;
		
		/** 
		 * default kWh consumption/100km - should be set based on true consumption data of users
		 */
		public static final int DEFAULT_GOAL = 20;

		/** 
		 * the how many-th waypoint should be plotted on the map 
		 * mapping every waypoint recorded is likely to overload the device
		 * 10 = every 10th waypoint
		 */
		public final static int DRAW_NTH_WAYPOINT = 20;
		
		/**
		 * maximum number of trips to draw
		 */
		public final static int N_TRIPS_TO_DRAW = 2;
		 
		public void updateMaxVehicleStillDuration() {
			try {
				// todo: check if this can be done using Integer.getInt() !
			this.maxVehicleStillDuration = PreferenceManager.getDefaultSharedPreferences(
					appContext).getString(PREF_MAX_VEHICLE_STILL_DURATION, MAX_VEHICLE_STILL_DURATION);
			L.d("maxVehicleStillDuration: " + maxVehicleStillDuration);
			}
			catch (Exception e) {
				L.e("Could not get maxVehicleStill from preferences. " + e.getCause());
				e.printStackTrace();
			}
		}
		
public Params(AppContext appContext) {
//	this.appContext = appContext;
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


public int getMaxVehicleStillLocation() {
	try {
		return Integer.parseInt(maxVehicleStillDuration);
	}
	catch (Exception e) {
		L.e("Could not parse maxVehicleStillDuration.");
		e.printStackTrace();
		return Integer.parseInt(MAX_VEHICLE_STILL_DURATION);
	}
}

public void setAppContext(AppContext appContext2) {
	this.appContext = appContext2;
}

public void setMaxVehicleStillLocation(Object newValue) {
	this.maxVehicleStillDuration = newValue.toString();
}


}

