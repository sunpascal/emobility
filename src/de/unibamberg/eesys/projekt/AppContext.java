package de.unibamberg.eesys.projekt;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.google.android.gms.location.DetectedActivity;

import de.unibamberg.eesys.projekt.businessobjects.Battery;
import de.unibamberg.eesys.projekt.businessobjects.ChargingStation;
import de.unibamberg.eesys.projekt.businessobjects.DriveSequence;
import de.unibamberg.eesys.projekt.businessobjects.Ecar;
import de.unibamberg.eesys.projekt.businessobjects.GeoCoordinate;
import de.unibamberg.eesys.projekt.businessobjects.WayPoint;
import de.unibamberg.eesys.projekt.database.DBImplementation;
import de.unibamberg.eesys.projekt.database.DatabaseException;
import de.unibamberg.eesys.projekt.gps.MobilityUpdater;
import de.unibamberg.eesys.projekt.gui.FragmentFolder;
import de.unibamberg.eesys.projekt.gui.GuiUpdateInterface;
import de.unibamberg.eesys.projekt.gui.activity.MainActivity;

/**
 * This is class which represents the applications context. It allows accessing
 * objects such as the database or the last reported GPS location.
 * 
 * Classes can access the context using the following:
 * 
 * AppContext appContext = (AppContext) getApplicationContext();
 * appContext.setEcar( // zum Aktualisieren des ausgewählten Fahrzeugs;
 * 
 * DatabaseImplementation db = appContext.s
 * (); // acess database
 * db.store***()
 * 
 * @author pascal
 */
public class AppContext extends Application {
	private static final String TAG = "AppContext";
	private MainActivity mainActivity; 
	private Params params; 
	
	public MainActivity getMainActivity() {
		return mainActivity;
	}

	public void setMainActivity(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	/**
	 * database object to access functionalities to load or store data in the DB
	 */
	private DBImplementation db;

	/**
	 * object which calculates energy consumed by a vehicle traveling from point
	 * A to point B
	 */
	private HlpEnergyConsumptionModel consumptionModel;

	/**
	 * contains the current location, speed, etc. Can be accessed by GUI.
	 */
	private WayPoint currentWayPoint;

	/**
	 * represents the current vehicle type and current state of the vehicle,
	 * incl. current battery state
	 */
	public Ecar ecar;

	/**
	 * interface defining how updates are passed from the app context to GUI
	 */
	public ArrayList<GuiUpdateInterface> listeners = new ArrayList<GuiUpdateInterface>();

	public static final boolean TESTING = false;

	/**
	 * List of known charging stations. Used to determine if vehicle should
	 * begin charging if a charge station is near. It is only loaded when the
	 * app is first started (it is not currently being updated automatically,
	 * e.i. hard coded)
	 */
	private List<ChargingStation> chargeStations;

	/**
	 * indicates the the gps sensor is enabled in android settings
	 */
	private boolean gpsEnabled;
	
	private int currentGpsStatus;

	public int test = 5;

	private boolean isPlayServicesAvailable;

	private static final String IGNORE_ACTIVITY_RECOGNITION = "testing.ignore_activity_recognition";
	
	// whether to show debug message - can be enabled in app settings menu
	private boolean showDebugMessages = false;

	public boolean isShowDebugMessages() {
		return showDebugMessages;
	}

	public void setShowDebugMessages(boolean showDebugMessages) {
		this.showDebugMessages = showDebugMessages;
	}

	/**
	 * key class which provides updates coming from the device's GPS sensor as
	 * well as status updates from Google Play services (which indicate if the
	 * phone is still, walking, driving, etc)
	 */
	private MobilityUpdater mobilityManager;
	
	private BackgroundService backgroundService;
	private Recommender recommender;
	
	private FragmentFolder fragmentFolder;
	
	/** 
	 * current kWh/100km consumption. 
	 * Either set by user or application default
	 * ToDo: persist!  
	 */
	private int goal = Params.DEFAULT_GOAL;
	
	/** 
	 * represents the consumption at the time when setting a new goal
	 * ToDo: persist!
	 */
	private double consumptionT0;	
	
	private DriveSequence lastTrip;
	
	static final String PREF_TESTING_ACTIVITY = "testing.activity";
	private static final String PREF_TESTING_SPEED = "testing.speed";
	
	public BackgroundService getBackgroundService() {
		return backgroundService;
	}

	public void setBackgroundService(BackgroundService backgroundService) {
		this.backgroundService = backgroundService;
	}
	
	/** 
	 * returns the lastTrip driven. Checks if it has been loaded and if not retrievs trip 
	 * from database
	 * 
	 *   after ending a trip, lastTrip is updated  
	 * @return
	 */
	public DriveSequence getLastTrip() {
			if (this.lastTrip != null)
				return this.lastTrip;
		
			List<DriveSequence> trips;
			try {
				trips = db.getDriveSequences(true);
				this.lastTrip = trips.get(trips.size()-1);
			} catch (DatabaseException e) {
				L.e("Could not get last trip due to database error.");
				e.printStackTrace();
				return null;
			} catch (IndexOutOfBoundsException e) {
				L.e("Could not get last trip. Are there any recorded trips?");
				return null;
			}
			
		return this.lastTrip;
	}
	

	@Override
	/**
	 * Executed upon first start of the app.
	 * A background service is started which runs continuously during the entire time (until app is killed). 
	 * Using a background service allows processing GPS signals even when the app is not active.  
	 */
	public void onCreate() {
		super.onCreate();

		// get database access object
		db = new DBImplementation(this);
		consumptionModel = new HlpEnergyConsumptionModel();
		mobilityManager = new MobilityUpdater(this);
		fragmentFolder = new FragmentFolder();
		
		// get list of all charging stations from database
		try {
			chargeStations = db.getChargingStations();
		} catch (DatabaseException e) {
			// TODO check exception handling
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

		// try to load ecar from database (warning: return empty object if there
		// is no ecar in database)
		try {
			ecar = db.getEcar();
		} catch (DatabaseException e) {
			// TODO check exception handling
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

		// if there is no ecar (app started for the first time)
		if (ecar.getName() == null) { // && ecar.getId() == -1
			ecar = new Ecar(this);
		}
		if (ecar.getVehicleType() == null)
			try {
				ecar.setVehicleType(db.getVehicleTypes().get(0));
			} catch (DatabaseException e) {
				// TODO check exception handling
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		if (ecar.getBattery() == null) {
			ecar.setBattery(new Battery());
			ecar.getBattery().setCurrentSoc(
					ecar.getVehicleType().getBatteryCapacity());
		}

		try {
			db.storeEcar(ecar);
		} catch (DatabaseException e) {
			// TODO check exception handling
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

		// start background service
		startBackgroundService();

		updateGui();

	}

	private void startBackgroundService() {
		startService(new Intent(this, BackgroundService.class));
	}
	
	public void stopBackgroundService() {
		stopService (new Intent(getBaseContext(), BackgroundService.class));
	}	

	public AppContext() {
		params = new Params(this);
		
	}

	public Ecar getEcar() {
		return ecar;
	}

	public void setEcar(Ecar ecar) {
		this.ecar = ecar;
		try {
			db.storeEcar(ecar);
		} catch (DatabaseException e) {
			// TODO check exception handling
			Toast.makeText(getApplicationContext(), "An unexpected error has occurred.",
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	public HlpEnergyConsumptionModel getConsumptionModel() {
		return consumptionModel;
	}

	public DBImplementation getDb() {
		return db;
	}

	public void setDb(DBImplementation db) {
		this.db = db;
	}

	public GeoCoordinate getCurrentPosition() {
		return currentWayPoint.getGeoCoordinate();
	}

	public void setCurrentWayPoint(WayPoint w) {
		this.currentWayPoint = w;
	}

	public WayPoint getCurrentWayPoint() {
		return currentWayPoint;
	}

	public List<ChargingStation> getChargeStations() {
		return chargeStations;
	}

	public void setChargeStations(List<ChargingStation> chargeStations) {
		this.chargeStations = chargeStations;
	}

	public void showNotification(String title, String content) {
		showNotification(title, content, 0);
	}

	/**
	 * displays an Android notification at the top of the screen that provides a
	 * shortcut to switch back to the app if it is in the background. Display
	 * current state and energy consumption. Originally intended for debugging
	 * purposes, but turned out to be a nice feature.
	 * 
	 * @param title
	 * @param content
	 * @param mId
	 */
	public void showNotification(String title, String content, int mId) {
		// prevent app crashes due to null accessing
		if (title == null && content == null)
			return;
		if (title == null)
			title = "";
		if (content == null)
			content = "";

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				getApplicationContext()).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(title).setContentText(content);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, MainActivity.class);

		// The stack builder object will contain an artificial back stack for
		// the started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(mId, mBuilder.build());
	}

	// called by activity to register to gps and activity updates
	public void setOnUpdateListener(GuiUpdateInterface listener) {
		// Store the listener object
		this.listeners.add(listener);
	}

	/**
	 * When there are GPS or Google play updates, this is called by the
	 * background service to update the GUI, reflecting current status, speed,
	 * energy consumption, etc. All this information is contained in a WayPoint
	 * object, which is simply passed to the GUI activity.
	 * 
	 */
	public void updateGui() {
		if (ecar != null) {
			overrideSpeedAndActivity(); // todo: code doubles
										// MObilityUpdater.sendUpdateToListeners(),
										// find better solution
			ecar.updateCarState();
		}

		for (GuiUpdateInterface listener : listeners) {
			listener.onGpsUpdate(currentWayPoint);
		}
	}

	/**
	 * When the user disabled GPS in the Android settings menu (e.g. to save
	 * battery), the MobilityUpdate class calls the background activity which
	 * passes on this information to the AppContext. The GUI is then called to
	 * update the GUI.
	 */
	public void showGpsDisabled() {
		for (GuiUpdateInterface listener : listeners) {
			listener.onGpsDisabled();
		}
	}

	/**
	 * When the user enabled GPS in the Android settings menu (e.g. to save
	 * battery), the MobilityUpdate class calls the background activity which
	 * passes on this information to the AppContext. The GUI is then called to
	 * update the GUI.
	 */
	public void showGpsEnabled() {
		for (GuiUpdateInterface listener : listeners) {
			listener.onGpsEnabled();
		}
	}
	
	/**
	 * When the GPS signal status changes, e.g. when signal is lost while car in a tunnel
	 * the MobilityUpdate class calls the background activity which
	 * passes on this information to the AppContext. The GUI is then called to
	 * update the GUI.
	 */
	public void statusChanged(int status) {
		for (GuiUpdateInterface listener : listeners) {
			listener.onStatusChanged(status);
		}
		currentGpsStatus = status;
	}
	/**
	 * 
	 * @return returns true if the GPS sensor is enabled in Android menu.
	 *         Android users can disable GPS to save energy.
	 */
	public boolean isGpsEnabled() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	


	/**
	 * used to check if user status updates are available (Google Play service).
	 * If not, the should not be taken into account when detecting car state
	 * changes.
	 * 
	 * @return
	 */
	public boolean ignoreActivityRecognition() {
		// if play services are not available, always ignore activity
		// recognition
		if (isPlayServicesAvailable == false)
			return true;
		// if it is available, use it except if it has been overriden in app
		// settings
		else {
			return !PreferenceManager.getDefaultSharedPreferences(this)
					.getString(IGNORE_ACTIVITY_RECOGNITION, "disabled")
					.equals("disabled");
		}

	}

	public void setPlayServicesAvailable(boolean available) {
		isPlayServicesAvailable = available;
	}

	public boolean isPlayServicesAvailable() {
		return isPlayServicesAvailable;
	}

	/**
	 * Use this to round a double to a nice number to be displayed in the GUI
	 * 
	 * @param d
	 *            a double with lots of decimal places (Nachkommastellen).
	 * @param decimalPlaces
	 *            the number of decimal places (Nachkommastellen) that you need.
	 * @return the nicely rounded number which can be shown in the GUI.
	 */
	public static String round(double d, int decimalPlaces) {
		String dfString = new String(new char[decimalPlaces]).replace("\0", "#");
		if (decimalPlaces == 0)
			dfString = "#";
		else 
			dfString = "#." + dfString;
		
	    DecimalFormat dForm = new DecimalFormat(dfString);
	    return dForm.format(d);
	}
	
	/**
	 * shorthand to round to 2 decimal places  
	 * @param d
	 * @return
	 */
	public static String round(double d) {
	    return round(d, 2);
	}	

	/**
	 * TimeTo100 is the duration needed until the vehicle's battery is fully
	 * recharged. As the user propably doesn't care how many milliseconds it
	 * will take, this function returns the duration in the human readable
	 * format: HH:mm:ss.
	 * 
	 * @return Remaining time until car is fully recharged in the format
	 *         HH:mm:ss.
	 */
	public String getTimeTo100Formatted() {
		double durationMs = ecar.getTimeTo100();

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss",
				Locale.getDefault());
		return sdf.format(new Date((long) (durationMs - TimeZone.getDefault()
				.getRawOffset())));
	}

	public MobilityUpdater getMobilityManager() {
		return mobilityManager;
	}

	public void setMobilityManager(MobilityUpdater mobilityManager) {
		this.mobilityManager = mobilityManager;
	}

	/**
	 * formats the date for use in GUI. Does not include time (e.g. 2015-03-08).
	 * 
	 * @param date
	 *            use timestamp.getTime() if you have a Timestamp object
	 * @return
	 */
	public String getFormattedDate(long date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDate = formatter.format(date);
		return formattedDate;
	}

	/**
	 * formats the date for use in GUI. Includes time.
	 * 
	 * @param date
	 *            use timestamp.getTime() if you have a Timestamp object
	 * @return
	 */
	public String getFormattedDateTime(long date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedDate = formatter.format(date);
		return formattedDate;
	}

	private void overrideSpeedAndActivity() {
		if (currentWayPoint == null)
			return;

		// overwrite values if testing (can be set in app settings)

		// ... activity recognition
		String testingActivity = PreferenceManager.getDefaultSharedPreferences(
				this).getString(PREF_TESTING_ACTIVITY, "disabled");

		if (!testingActivity.equals("disabled")) {
			currentWayPoint.setActivityConfidence(-99);

			int activityType = DetectedActivity.UNKNOWN;
			if (testingActivity.equals("in_vehicle"))
				activityType = DetectedActivity.IN_VEHICLE;
			else if (testingActivity.equals("still"))
				activityType = DetectedActivity.STILL;

			this.getCurrentWayPoint().setActivity(activityType);
		}

		// ... speed
		String testingSpeed = PreferenceManager.getDefaultSharedPreferences(
				this).getString(PREF_TESTING_SPEED, "disabled");

		if (!testingSpeed.equals("disabled")) {
			double v = Double.parseDouble(testingSpeed) / 3.6;
			currentWayPoint.setVelocity(v);
		}
	}

	public int getCurrentGpsStatus() {
		return currentGpsStatus;
	}

	// todo: kann wieder gel�scht werden, da Aufruf �ber getActivity() gel�st
	public void showDrawer(int name) {
		mainActivity.selectItem(name);
		
	}
	
	public Recommender getRecommender() {
		if (recommender == null)
			recommender = new Recommender(this); 
		return recommender;
	}

	public Params getParams() {
		return params;
	}

	public FragmentFolder getFragmentFolder() {
		return fragmentFolder;
	}

	public void setGoal(int value) {
		this.goal = value; 
	}

	public int getGoal() {
		return goal;
	}

	public void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	public void shutdown() {
		
		try {
			db.storeEcar(ecar);
//			ecar.endTripAbnormal();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}		
		stopBackgroundService();
		getMainActivity().finish();
	}

	/** 
	 * used to update the last trip in app context to prevent constant database access
	 * @param lastTrip
	 */
	public void setLastTrip(DriveSequence lastTrip) {
		this.lastTrip = lastTrip;
	}

	public double getConsumptionT0() {
		return consumptionT0;
	}

	public void setConsumptionT0(double consumptionT0) {
		this.consumptionT0 = consumptionT0;
	}
	
}
