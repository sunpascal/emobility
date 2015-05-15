package de.unibamberg.eesys.projekt;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
import de.unibamberg.eesys.projekt.businessobjects.Ecar.CarState;
import de.unibamberg.eesys.projekt.businessobjects.WayPoint;
import de.unibamberg.eesys.projekt.database.DatabaseException;
import de.unibamberg.eesys.projekt.database.DatabaseImplementationTest;
import de.unibamberg.eesys.projekt.gps.GpsUpdateListener;
import de.unibamberg.eesys.projekt.gps.MobilityUpdater;

/**
 * Background service which runs constantly during the entire time, even if the
 * app is not in foreground. This allows capturing GPS signals even if the user
 * is using another app (ToDo: what about standby?)
 * 
 * The background service receives GPS sensor and status data from
 * MobilityManager and makes it available in the AppContext which also takes
 * care of updating the GUI. This is implemented using a listener receiving
 * MobilityManager updates and a GUI listeners registered in AppContext.
 * 
 * todo: was passiert eigentlich wenn charge state = 0 ? was wenn die einzelnen
 * Koordinaten eines Trips gespeichert wurden, aber nicht der Trip in der DB
 * angelegt wurde? -> Recovery mechanismus?
 * 
 * @author pascal
 *
 */
public class BackgroundService extends Service implements GpsUpdateListener {

	/**
	 * Car states DRIVING - ongoing trip CHARGING - Stopped, charging (triggered
	 * by nearby charge station) PARKING_NOT_CHARGING - Stopped, not Charging
	 * (no charge station nearby)
	 */

	public final static String INTENT_ACTION_UPDATE_GUI = "GuiUpdate";
	public final static String INTENT_EXTRA_WAYPOINT = "waypoint";
	public final static String INTENT_EXTRA_ENERGY = "energy";

	private AppContext appContext;
	private ArrayList<WayPoint> buffer;
	private static final int BUFFER_MAX_LENGTH = 1;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * called when the BackgroundService is started - which is when the app is
	 * opened for the first time after a system restart (=> service start
	 * triggered in AppContext)
	 */
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		// store reference to AppContext to use underlying functions and access
		// database.
		appContext = (AppContext) getApplicationContext();
		appContext.setBackgroundService(this);

		new DatabaseImplementationTest(appContext.getDb(), appContext)
				.testDatabase(); // @Stefan

		// reference to mobility manager which provides GPS and status updates.
		MobilityUpdater mobilityManager = appContext.getMobilityManager();

		// GPS & activity recognition:
		// register for gps+activity updates - this will cause onGpsUpdate() to
		// be called at each update
		mobilityManager.setOnUpdateListener(this);

		// Main entry point //

		// Todo: check if an existing trip has been started before and resume if
		// necessary

		buffer = new ArrayList<WayPoint>();

		return START_STICKY; // keep service going until explicitly stopped
	}
	/**
	 * called when a new wayPoint is detected by GPS
	 * @param w = new WayPoint
	 */
	public void onWayPointUpdate(WayPoint w) {
		appContext.setCurrentWayPoint(w);

		appContext.getEcar().processLifecycle();

		// record coordinates and write to buffer for storing in DB
		if (appContext.getEcar().getCarState() == CarState.DRIVING) {
			captureLocation();
		}

		appContext.updateGui();

	}

	/**
	 * called at every new GPS position. New waypoints are not persisted
	 * immediately to prevent constantly accessing the database on the sdcard.
	 * Instead they are written to a buffer and persisted every time there are
	 * BUFFER_MAX_LENGTH objects in the buffer.
	 * 
	 * To improve performance, BUFFER_MAX_LENGTH should be increased.
	 * 
	 * ToDo: Replace with more periodic updates, since if vehicle stops, there
	 * will be no new updates, preventing the buffer from ever getting full and
	 * its contents written to the database!!!
	 */
	private void captureLocation() {
		WayPoint w = appContext.getCurrentWayPoint();

		if (w != null) {
			buffer.add(w);
		}

		// fill buffer with coordinates
		if (buffer.size() >= BUFFER_MAX_LENGTH) {
			// flush buffer by writing entire contents to DB and remove
			flushBuffer();
		}

	}

	/**
	 * empty the buffer and store contents in database.
	 */
	public void flushBuffer() {

		if (!buffer.isEmpty()) {
			try {
				appContext.getDb().storeWayPoints(
						buffer.toArray(new WayPoint[0]));
			} catch (DatabaseException e) {
				Toast.makeText(getApplicationContext(),"An unexpected error has occurred.",
						Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
			buffer.clear();

		}
		try {
			appContext.getDb().storeEcar(appContext.getEcar());
		} catch (DatabaseException e) {
			L.e("Could not store ecar (and battery)");
			Toast.makeText(appContext, "An unexpected error has occurred.",
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	public void onDestroy() {
		super.onDestroy();
		// Todo: do not stop if settings view is closed!!!
	}

	@Override
	public void onGpsDisabled() {
		appContext.showGpsDisabled();
	}

	@Override
	public void onGpsEnabled() {
		appContext.showGpsEnabled();
	}

	@Override
	public void onStatusChanged(int status) {
		appContext.statusChanged(status);

	}

}
