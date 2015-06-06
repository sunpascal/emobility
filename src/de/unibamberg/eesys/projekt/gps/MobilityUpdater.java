package de.unibamberg.eesys.projekt.gps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.DetectedActivity;

import de.unibamberg.eesys.projekt.AppContext;
import de.unibamberg.eesys.projekt.HlpEnergyConsumptionModel;
import de.unibamberg.eesys.projekt.L;
import de.unibamberg.eesys.projekt.businessobjects.Ecar.CarState;
import de.unibamberg.eesys.projekt.businessobjects.GeoCoordinate;
import de.unibamberg.eesys.projekt.businessobjects.WayPoint;
import de.unibamberg.eesys.projekt.businessobjects.WayPoint.UpdateType;
import de.unibamberg.eesys.projekt.gui.GuiUpdateInterface;

/**
 * implements: ConnectionCallbacks: for activity recognition: Methods called
 * when connection to Google Play Services established/disconnected
 * OnConnectionFailedListener: for activity recognition: Google Play Services
 * LocationListener: needed to receive GPS location updates
 */
public class MobilityUpdater implements LocationListener, ConnectionCallbacks,
		OnConnectionFailedListener {

	public ArrayList<GpsUpdateListener> listeners = new ArrayList<GpsUpdateListener>();

	private AppContext appContext;

	// flag for GPS status
	boolean isGPSEnabled;

	// flag for network status
	boolean isNetworkEnabled;
	boolean canGetLocation;
	
	GuiUpdateInterface guiInterface;
	Location location;
	double latitude;
	double longitude;
	protected LocationManager locationManager;
	public Location previousLocation;
	public double startVelocity = 0d;
	public double startAltitude = 0d;
	double energy = 0;
	boolean gpsDisabled = false;

	public long lastLocationTime;
	float distanceCumulated = 0.0f;
	
	// update GPS every GPS_UPDATE_TIME seconds
	// must be 1s reflect
	
	// 5 second mean 138m at 100 km/h (not much on the Highway) 
	// 			...   70m at 50 km/h
	//			...   27m at 20 km/h
	
	// idea: use dynamic interval depending on speed of last waypoint
	private static final long GPS_UPDATE_TIME = 1*1000;
	
	/**
	 * minimum distance between two points for a GPS point to be counter
	 */
	private static final long UPDATE_DISTANCE = 0;		// use 0 in order to detect when car has stopped
	
	//Verzögerung die gewartet wird, bis die neue Activity umgestellt wird
	// currently only every 10 minutes (de facto disabled), before: 1s 
	private static final long ACTIVITY_RECOGNITION_UPDATE_TIME = 600*1000;		 
//	private static final long ACTIVITY_RECOGNITION_UPDATE_TIME = 360000000;

	private static final String PREF_TESTING_ACTIVITY = "testing.activity";
	private static final String PREF_TESTING_SPEED = "testing.speed";

	private HlpEnergyConsumptionModel consumptionModel;
	private ActivityRecognitionClient mActivityRecognitionClient;
	private ActivityUpdateReceiver activityUpdateReceiver;

	private final Context mContext;
	private WayPoint waypoint = null;
	
	public class ActivityUpdateReceiver extends BroadcastReceiver {

		@Override
		/** 
		 * called when there is an Activity Recognition update 
		 */
		public void onReceive(Context context, Intent intent) {

			waypoint.setUpdateType(UpdateType.ACTIVITY_RECOGNITION);

			// use true activity value
			if (intent.hasExtra("activityType"))
				waypoint.setActivityType(intent.getIntExtra("activityType", -1));
			if (intent.hasExtra("confidence"))
				waypoint.setActivityConfidence(intent.getIntExtra("confidence",
						-1));

			sendUpdateToListeners();
		}
	}

	public MobilityUpdater(Context a) {
		this.mContext = a;

		waypoint = new WayPoint();

		// set up GPS location retrieval
		locationManager = (LocationManager) a
				.getSystemService(Context.LOCATION_SERVICE);

		LocationListener mlocListener = this;
		// locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
		// GPS_UPDATE_TIME, UPDATE_DISTANCE, mlocListener);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				GPS_UPDATE_TIME, UPDATE_DISTANCE, mlocListener);

		appContext = (AppContext) a.getApplicationContext();
		consumptionModel = appContext.getConsumptionModel();

		// try to get last known location when starting app
		// commented out to prevent big jumps from LastKnownLocation to actual
		// location (and thus unreastically high energy consumption)
		// getLastLocation();

		// set up activity recognition
		activityUpdateReceiver = new ActivityUpdateReceiver();

		// register BroadcastReceiver for activity updates
		IntentFilter intentFilter_update = new IntentFilter(
				ActivityUpdateForwarder.INTENT_ACTION);
		intentFilter_update.addCategory(Intent.CATEGORY_DEFAULT);
		mContext.registerReceiver(activityUpdateReceiver, intentFilter_update);

		// Connect to the ActivityRecognitionService
		mActivityRecognitionClient = new ActivityRecognitionClient(
				(Context) mContext, this, this);
		mActivityRecognitionClient.connect();
		
	}

	/**
	 * loads multiple test files from /assets folder to simulate driving **
	 */
	public void loadTestDataFromGpx() {
		GpxLoader gpxLoader = new GpxLoader(appContext);

		// use this to load all .gpx files in /assets
		String[] listOfFiles;
		try {
			listOfFiles = appContext.getResources().getAssets().list("");
			for (String filename : listOfFiles) {
				if (filename.endsWith(".gpx")) {
					L.d("gpx file found: " + filename);
					List<Location> locations = gpxLoader.loadGpx(filename);
					simulateLocations(locations, false);
					// set battery state to full
					appContext.getEcar().getBattery().setCurrentSoc(appContext.getEcar().getVehicleType().getBatteryCapacity());
				}
			}
		} catch (IOException e) {
			L.e("Could not get list of *.gpx files");
			e.printStackTrace();
		}

//		 List<Location> locations = gpxLoader.loadGpx("Track201501202031.gpx");
//		 simulateLocations(locations, false);

		// List<Location> locations = gpxLoader.loadGpx("Track201501211158.gpx");
		// simulateLocations(locations, false);

//		 List<Location> locations = gpxLoader.loadGpx("Track201501211422.gpx");
		// simulateLocations(locations, false);

		// List<Location> locations = gpxLoader.loadGpx("Track201501220823.gpx");
		// simulateLocations(locations, false);

//		List<Location> locations = gpxLoader.loadGpx("Track201501221730.gpx");
//		simulateLocations(locations, false);

		// List<Location> locations = gpxLoader.loadGpx("Track201501051332.gpx");
		// simulateLocations(locations, false);

//		 L.d("List of trips:");
//		 for (de.unibamberg.eesys.projekt.businessobjects.DriveSequence trip : appContext.getDb().getDriveSequences())
//		 L.v(appContext.getFormattedDate(trip.getTimeStart()) + " " + AppContext.round(trip.getCoveredDistance()/1000, 2) + "km");

	}

	
	/** 
	 * Android allows retrieving the last known location. 
	 * 
	 * If GPS is not available, other sources such as the network carrier 
	 * can be used to get an approximate position. This should propably 
	 * not be fed into the consumption data as it is not very exact (blue radius in
	 * Google Maps) 
	 *  
	 */
	private void getLastLocation() {
		// get from GPS...
		Location lastKnownLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		// if not available get from Wifi / cellular network...
		if (lastKnownLocation == null)
			lastKnownLocation = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		if (lastKnownLocation != null)
			processLocation(lastKnownLocation);
	}

	// called by activity to register to gps and activity updates
	public void setOnUpdateListener(GpsUpdateListener listener) {
		// Store the listener object
		this.listeners.add(listener);
	}

	// Called when a connection to the ActivityRecognitionService has been
	// established.
	public void onConnected(Bundle connectionHint) {
		Intent intent = new Intent(mContext, ActivityUpdateForwarder.class);
		PendingIntent callbackIntent = PendingIntent.getService(mContext, 5,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		mActivityRecognitionClient.requestActivityUpdates(
				ACTIVITY_RECOGNITION_UPDATE_TIME, callbackIntent);
		appContext.setPlayServicesAvailable(true);
		L.v("Connected to Google Play Services and waiting for activity recognition updates.");
	}

	@Override
	/**  
	 * called when there is a GPS location update
	 */
	public void onLocationChanged(Location newLocation) {

		/*
		 * ToDo: - decide which location update to use =>
		 * http://developer.android.com/guide/topics/location/strategies.html -
		 * use NETWORK_PROVIDER if user has GPS turned off
		 */

		waypoint.setUpdateType(UpdateType.GPS);
		
		// overwrite values if testing (can be set in app settings)

		// ... activity recognition
		String testingActivity = PreferenceManager.getDefaultSharedPreferences(
				appContext).getString(PREF_TESTING_ACTIVITY, "disabled");

		if (!testingActivity.equals("disabled")) {
			waypoint.setActivityConfidence(-99);

			int activityType = DetectedActivity.UNKNOWN;
			if (testingActivity.equals("in_vehicle"))
				activityType = DetectedActivity.IN_VEHICLE;
			else if (testingActivity.equals("still"))
				activityType = DetectedActivity.STILL;

			waypoint.setActivity(activityType);
		}

		// ... speed
		String testingSpeed = PreferenceManager.getDefaultSharedPreferences(
				appContext).getString(PREF_TESTING_SPEED, "disabled");

		if (!testingSpeed.equals("disabled")) {
			float v_kmh = Float.parseFloat(testingSpeed);
			newLocation.setSpeed((float) (v_kmh / 3.6));
		}
		
		processLocation(newLocation);
	}

	/** 
	 * process locations 
	 * 
	 * @param currentLocation   current location at locationChange 
	 * 							previousLocation: last location
	 */
	private void processLocation(Location currentLocation) {
		// some values may no have speed - energy calculation will not make sense 
		// and default value of v=0 will cause ongoing trip to stop!  
		if (currentLocation.hasSpeed() == false) {
			L.v("Skipped one location update since velocity was not available.");
			return; 
		}
		double endVelocity = 0.0;
		double endAltitude = 0; 
		float distanceDelta = 0.0f;
		double altitudeDiffInMeters = 0.0f;
		float timeStampDiffInSeconds = 0.0f;
		double Acceleration = 0.0;

		// first coordinate captured
		if (previousLocation == null) {
			previousLocation = currentLocation;
		} else {
			// two or more coordinates captured
			distanceDelta = calcDistance(previousLocation, currentLocation);
			distanceCumulated = distanceCumulated + distanceDelta;

			// use speed supplied by GPS sensor
			endVelocity = currentLocation.getSpeed();

			timeStampDiffInSeconds = calcTimeStampDiff(previousLocation, currentLocation);
			
			altitudeDiffInMeters = endAltitude - startAltitude;

			if (startVelocity == 0) {
				Acceleration = 0;
			} else {
				Acceleration = calcAcceleration(startVelocity, endVelocity,
						timeStampDiffInSeconds);
			}

			// get energy consumed between the two points

			if (timeStampDiffInSeconds == 0 || distanceDelta == 0 || appContext.getEcar().getCurrentTrip() == null) {
				// when trip has not been started (startTrip called in Ecar), the vehicleType will not yet have been added 
				// to the current trip. Therefore skip energy computation for first waypoint.
				L.v("Not all data available, energy computation does not make sense");
				energy = 0;
			} else {
				energy = consumptionModel.consumeEnergy(timeStampDiffInSeconds, 
														startVelocity,
														endVelocity, 
														distanceDelta, 
														Acceleration, 
														altitudeDiffInMeters,
														appContext.getEcar().getCurrentTrip().getVehicleType());
			}

			L.v(
					distanceDelta + "m " + timeStampDiffInSeconds + "s "
					+ AppContext.round(startVelocity * 3.6, 1)
					+ "km/h (startVelocity) "
					+ AppContext.round(endVelocity * 3.6, 1)
					+ "km/h (endVelocity)  "
					+ AppContext.round(Acceleration * 3.6, 1) + "km/h/s "
					+ AppContext.round(energy, 2) + "kWh "
					+ waypoint.getUpdateType()
			);
			if (appContext.getEcar().getCurrentTrip() != null && appContext.getEcar().getCurrentTrip().getVehicleType() != null) 
				L.v(appContext.getEcar().getCurrentTrip().getVehicleType().getName() + 
						" " + appContext.getEcar().getCurrentTrip().getVehicleType().getId());

			startVelocity = endVelocity;
			startAltitude = endAltitude; 

			previousLocation = currentLocation;

		}

		waypoint.setDistance(distanceDelta);
		waypoint.setAcceleration(Acceleration);

		waypoint.setVelocity(startVelocity);

		GeoCoordinate geo = new GeoCoordinate(currentLocation.getLongitude(),
				currentLocation.getLatitude(), currentLocation.getAltitude());
		waypoint.setGeoCoordinate(geo);
		waypoint.setTimestamp(currentLocation.getTime());
		waypoint.setEnergy(energy);

		sendUpdateToListeners();
	}

	/**
	 * calls update method on all listeners listeners will receive gps data
	 * object which they can use to update the GUI
	 */
	private void sendUpdateToListeners() {

		// allow accessing current drive sequence via waypoint object
		if (appContext.getEcar() != null && appContext.getEcar().getCurrentTrip() != null)
			waypoint.setDriveSequence(appContext.getEcar().getCurrentTrip());

		// send update to listernes
		if (waypoint != null)
			for (GpsUpdateListener listener : listeners) {
				listener.onWayPointUpdate(waypoint);
			}
		
		L.v(" " + waypoint.getGeoCoordinate().toString() + 
				" " + AppContext.round(waypoint.getVelocity()*3.6, 2) + "km/h " +
				" " + AppContext.round(waypoint.getAcceleration()*3.6, 2) + "km/h/s " + 
				" " + AppContext.round(waypoint.getDistance(), 2) + "m " +
				AppContext.round(waypoint.getEnergyInKWh(), 2) + "kWh");		
	}

	/**
	 * calculate acceleration 
	 * @param startVelocity velocity at first waypoint
	 * @param endVelocity velocity at second waypoint
	 * @param timeStampDiffInSeconds time difference in seconds
	 * @return acceleration meter/second
	 */
	public double calcAcceleration(double startVelocity, double endVelocity,
			float timeStampDiffInSeconds) {
		// Beschleunigung = (Endgeschwindigkeit - Anfangsgeschwindigkeit) /
		// Zeitdauer
		double acceleration = (endVelocity - startVelocity)
				/ timeStampDiffInSeconds;

		return acceleration;

	}

	/**
	 *
	 * @param lastLoc last location
	 * @param newLoc current location
	 * @param distance 
	 *            in m
	 * @return speed in m/s
	 */
	public double calcVelocity(Location lastLoc, Location newLoc, float distance) {
		float timeStampDiffInSeconds = 0.0f;
		timeStampDiffInSeconds = (newLoc.getTime() - lastLoc.getTime()) / 1000;
		double velocity = (distance / timeStampDiffInSeconds); // in m/s
		return velocity;
	}

	/**
	 * 
	 * @param lastLoc last location
	 * @param newLoc current location
	 * @return time difference in seconds between two location points
	 */
	public float calcTimeStampDiff(Location lastLoc, Location newLoc) {
		float timeStampDiffInSeconds = 0.0f;
		timeStampDiffInSeconds = (newLoc.getTime() - lastLoc.getTime()) / 1000;
		return timeStampDiffInSeconds;
	}
	

	/**
	 * called when a connection to google play services for activity recognition
	 * has failed
	 */
	public void onConnectionFailed(ConnectionResult arg0) {
		appContext.setPlayServicesAvailable(false);
		Toast.makeText(
				mContext.getApplicationContext(),
				"Could not connect to Google Play Services. Activities will not be recognized.",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDisconnected() {
	}

	@Override
	/**
	 * is called when user disabled gps in menu
	 * => is not called when signal is lost (according to Julia)
	 */
	public void onProviderDisabled(String provider) {
		String toastMessage = "Gps provider disabled."; 
		L.d(toastMessage);
		
		if (appContext.isShowDebugMessages())
			Toast.makeText(
				mContext.getApplicationContext(),
				toastMessage,
				Toast.LENGTH_SHORT).show();					
		
		for (GpsUpdateListener listener : listeners) {
			listener.onGpsDisabled();
		}		
		 // Todo: change status to parking, e.g. by sending a waypoint with v=0
				appContext.getEcar().endTripAbnormal();
		}
	

	@Override
	public void onProviderEnabled(String provider) {
		if (appContext.isShowDebugMessages()) {
			String toastMessage = "Gps provider enabled."; 
			L.d(toastMessage);
			Toast.makeText(
				mContext.getApplicationContext(),
				toastMessage,
				Toast.LENGTH_SHORT).show();
		}
		
		for (GpsUpdateListener listener : listeners) {
			listener.onGpsEnabled();
		}	
	}

	@Override
	/** 
	 is called when signal is lost or gps becomes available
	 * status = 0  
	 */
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
		if (appContext.isShowDebugMessages()) {
		String toastMessage = "onStatusChanged - status: "; 
		if (status == LocationProvider.OUT_OF_SERVICE) {
			toastMessage += "OUT_OF_SERVICE- Ending trip";
		}
		else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
			// Todo: once everything is teseted - consider not ending the trip because gps signal may come back
			toastMessage += "TEMPORARILY_UNAVAILABLE - Ending trip";
		}
		else if (status == LocationProvider.AVAILABLE) {
			toastMessage += "AVAILABLE";
		}
		Toast.makeText(
				mContext.getApplicationContext(),
				toastMessage,
				Toast.LENGTH_SHORT).show();	
		}
		
		L.d("Gps provider onStatusChanged");
		for (GpsUpdateListener listener : listeners) {
			listener.onStatusChanged(status);
		}
		
		if (status == LocationProvider.OUT_OF_SERVICE) {
			// end trip
			if (appContext.getEcar().getCarState() == CarState.DRIVING)
				appContext.getEcar().endTripAbnormal();
		}
		else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
			// do not do anything since signal may come back
		}
		else if (status == LocationProvider.AVAILABLE) {
		}
	}
	
	/**
	 * calculate distance between two locations 
	 * 
	 * @param lastLoc last location data
	 * @param newLoc current location data
	 * @return distance in meter
	 */
	public float calcDistance(Location lastLoc, Location newLoc) {
		float distance = 0.0f;
		float[] result = new float[3];
		Location.distanceBetween(lastLoc.getLatitude(), lastLoc.getLongitude(),
				newLoc.getLatitude(), newLoc.getLongitude(), result);
		distance = result[0];
		return distance;
	}
	
	public void simulateLocations(List<Location> locations, boolean useTestProvider) {
		
		// manipulate last location with speed = 0 so that trip ends (and is
		// stored in DB)
		Location lastLocation = locations.get(locations.size() - 1);
		lastLocation.setSpeed(0);
		
		// da beim Einlesen über MockProvider einige GPS Updates übersprungen werden und somit 
		// nicht alles in die Datebank geschrieben werden würde, kann man auch direkt den WayPoint erzeugen
		if (!useTestProvider) {
			for (Location l : locations) {
				processLocation(l);
			}
		}
		else 
		{
			// Location Update for mock locations
			locationManager.addTestProvider(LocationManager.GPS_PROVIDER,
					"requiresNetwork" == "", "requiresSatellite" == "",
					"requiresCell" == "", "hasMonetaryCost" == "",
					"supportsAltitude" == "", "supportsSpeed" == "",
					"supportsBearing" == "", android.location.Criteria.POWER_LOW,
					android.location.Criteria.ACCURACY_FINE);
			locationManager.setTestProviderStatus(LocationManager.GPS_PROVIDER,
					LocationProvider.AVAILABLE, null, 1000);
			locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER,
					true);
	
			// set mock locations
			for (Location l : locations) {
				// use current time instead of time from .gpx (timestamps in the past will not work?)
				l.setTime(System.currentTimeMillis());
				
				// if no location updates are received, try adjusting GPS_UPDATE_TIME and UPDATE_DISTANCE
				locationManager.setTestProviderLocation(
						LocationManager.GPS_PROVIDER, l);
			}
	
			// remove test provider to resume receiving real GPS coordinates
			locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
		}

	}
}
