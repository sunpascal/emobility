package de.unibamberg.eesys.projekt.gui;

import android.support.v4.app.Fragment;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.LocationProvider;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import de.unibamberg.eesys.projekt.AppContext;
import de.unibamberg.eesys.projekt.L;
import de.unibamberg.eesys.projekt.R;
import de.unibamberg.eesys.projekt.businessobjects.Ecar;
import de.unibamberg.eesys.projekt.businessobjects.Ecar.CarState;
import de.unibamberg.eesys.projekt.businessobjects.WayPoint;

/**
 * Fragment for Main Overview with battery display View Overview in three
 * different versions according to the car state (driving, charging, parking)
 * 
 * @author Julia
 *
 */
public class DashboardFragment extends Fragment implements GuiUpdateInterface,
		OnTouchListener, OnClickListener {
	public static final String TAG = "StatusFragment";
	public static final String ARG_STATUS = "status";
	public String GPS_NO_SIGNAL = "GPS has no signal.";
	public String GPS_NO_SIGNAL_TEMPORARILY = "GPS has no signal (temporarily).";
	public String GPS_DISABLED = "GPS is currently disabled in Android settings.";

	private int threshholdGreenAcceleration = 20; // if current consumption is > 200 kWh, background should be red 
	
	AppContext appContext;

	// TextViews of Status Fragment displayed in fragment_batterydisplay.xml
	TextView txtAkkuState;
	TextView txtCarState;
	TextView txtCurrentSpeed;
	TextView txtCurrentSpeedLbl;
	TextView txtCurrentConsumption;
	TextView txtCoveredDistance;
	TextView txtCoveredDistanceLbl;
	TextView txtRemainingKmOnBatteryLbl;
	TextView txtRemainingKmOnBattery;
	TextView txtTimeTo100;
	TextView txtTimeTo100Lbl;
	TextView txtGPSDisabled;
	
	ProgressBar progressBar1;

	// Debug TextViews only Visible in Debug Mode not visible to user
	TextView txtDebug1;
	TextView txtDebug2;
	TextView txtDebug3;
	
	private View rootView;

	/**
	 * Fragment Class Constructor
	 */
	public DashboardFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	/** 
	 * called when switching to status fragment (i.e. after having viewed another menu tab (i.e. drivegraph))
	 * Status Fragment is initial view after app is started 
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_dashboard,
				container, false);

		appContext = (AppContext) getActivity().getApplicationContext();
		appContext.setOnUpdateListener(this);

		// Setting TextViews (static TextViews)
		txtAkkuState = (TextView) rootView.findViewById(R.id.textview_text_current_charginglvl);
		txtCarState = (TextView) rootView.findViewById(R.id.textview_text_state);

		// Setting TextViews (dynamical TextViews)
		// visible if CarState = driving
		txtCoveredDistance = (TextView) rootView.findViewById(R.id.textview_text_coveredDistance);
		txtCoveredDistanceLbl = (TextView) rootView.findViewById(R.id.textView_label_coveredDistance);
		txtCurrentSpeed = (TextView) rootView.findViewById(R.id.textview_text_currentSpeed);
		txtCurrentSpeedLbl = (TextView) rootView.findViewById(R.id.textView_label_currentSpeed);

		txtCurrentConsumption = (TextView) rootView.findViewById(R.id.textview_text_currentConsumption);
		
		txtRemainingKmOnBattery = (TextView) rootView.findViewById(R.id.textview_text_remainingTimeOnBattery);
		txtRemainingKmOnBatteryLbl = (TextView) rootView.findViewById(R.id.textView_label_remainingTimeOnBattery);

		// visible if CarState = charging
		txtTimeTo100 = (TextView) rootView.findViewById(R.id.textview_text_timeTo100);
		txtTimeTo100Lbl = (TextView) rootView.findViewById(R.id.textView_label_timeTo100);
		
		progressBar1 = (ProgressBar) rootView.findViewById(R.id.progressBar1);

		// visible if GPS Disabled
		txtGPSDisabled = (TextView) rootView.findViewById(R.id.textview_text_GPSDisabled);

		// debug TextViews
		txtDebug1 = (TextView) rootView.findViewById(R.id.debug1);
		txtDebug2 = (TextView) rootView.findViewById(R.id.debug2);
		txtDebug3 = (TextView) rootView.findViewById(R.id.debug3);

		showCarStateView(appContext.getEcar().getCarState());

		// process charging updates
		appContext.ecar.processLifecycle();

		// refresh GUI to reflect charging updates, and location updates that
		// may have been received in the mean time
		refreshGui(appContext.getCurrentWayPoint());

		if (!appContext.isGpsEnabled()) {
			txtGPSDisabled.setText(GPS_DISABLED);
			txtGPSDisabled.setVisibility(View.VISIBLE);
		}

		return rootView;
	}

	@Override
	public void onAkkuUpdate(String akkuValue) {
		Log.v(TAG, "newSpeed value = " + akkuValue);
		txtAkkuState.setText(akkuValue);
	}

	@Override
	public void onSpeedUpdate(String speed) {
		Log.v(TAG, "newSpeed value = " + speed);
		txtCurrentSpeed.setText(speed);
	}

	@Override
	public void onConsumptionUpdate(String consumedEnergy) {
		Log.v(TAG, "newSpeed value = " + consumedEnergy);
		txtCurrentConsumption.setText(consumedEnergy);
	}

	@Override
	public void onRemainingTimeOnBatteryUpdate(String timeOnBattery) {
		Log.v(TAG, "newSpeed value = " + timeOnBattery);
		txtRemainingKmOnBattery.setText(timeOnBattery);
	}

	@Override
	public void onCharchingTimeRemainingUpdate(String time) {
		Log.v(TAG, "newSpeed value = " + time);
		txtTimeTo100Lbl.setText(time);
	}

	@Override
	public void onCoveredDistanceUpdate(String coveredDistance) {
		Log.v(TAG, "newSpeed value = " + coveredDistance);
		txtAkkuState.setText(coveredDistance);
	}

	@Override
	public void onCarStateUpdate(CarState carState) {
		if (carState == null)
			return;
		showCarStateView(carState);
	}

	/**
	 * shows different GUI elements depending on the current car state
	 * 
	 * @param carState
	 *            = either driving, charging or parking
	 */
	private void showCarStateView(CarState carState) {
		if (carState == Ecar.CarState.DRIVING) {
			txtCurrentConsumption.setVisibility(View.VISIBLE);
//			txtCurrentSpeed.setVisibility(View.VISIBLE);
//			txtCurrentSpeedLbl.setVisibility(View.VISIBLE);
			txtCoveredDistance.setVisibility(View.VISIBLE);
			txtCoveredDistanceLbl.setVisibility(View.VISIBLE);
			txtRemainingKmOnBattery.setVisibility(View.VISIBLE);
//			txtRemainingKmOnBatteryLbl.setVisibility(View.VISIBLE);  // always hide label for remaining km
			txtTimeTo100.setVisibility(View.GONE);
			txtTimeTo100Lbl.setVisibility(View.GONE);

		} else if (carState == Ecar.CarState.CHARGING) {
			// reset background color to white
			rootView.setBackgroundColor(getResources().getColor(R.color.color_white));	
			
			txtCurrentConsumption.setVisibility(View.GONE);
//			txtCurrentSpeed.setVisibility(View.GONE);
//			txtCurrentSpeedLbl.setVisibility(View.GONE);
			txtCoveredDistance.setVisibility(View.GONE);
			txtCoveredDistanceLbl.setVisibility(View.GONE);
			txtRemainingKmOnBattery.setVisibility(View.VISIBLE);
//			txtRemainingKmOnBatteryLbl.setVisibility(View.VISIBLE);  // always hide label for remaining km
			txtTimeTo100.setVisibility(View.VISIBLE);
			txtTimeTo100Lbl.setVisibility(View.VISIBLE);

		} else if (carState == Ecar.CarState.PARKING_NOT_CHARGING) {
			// reset background color to white
			rootView.setBackgroundColor(getResources().getColor(R.color.color_white));
			
			txtCurrentConsumption.setVisibility(View.GONE);
//			txtCurrentSpeed.setVisibility(View.GONE);
//			txtCurrentSpeedLbl.setVisibility(View.GONE);
			txtCoveredDistance.setVisibility(View.GONE);
			txtCoveredDistanceLbl.setVisibility(View.GONE);
			txtRemainingKmOnBattery.setVisibility(View.GONE);
//			txtRemainingKmOnBatteryLbl.setVisibility(View.GONE);
			txtTimeTo100.setVisibility(View.GONE);
			txtTimeTo100Lbl.setVisibility(View.GONE);
		}

		txtDebug1.setVisibility(View.VISIBLE);
		txtDebug2.setVisibility(View.VISIBLE);
		txtDebug3.setVisibility(View.VISIBLE);

	}

	@Override
	/**
	 * Notification to the user when GPS is disabled with toast and textView message on the overview
	 */
	public void onGpsDisabled() {
		txtGPSDisabled.setText(GPS_DISABLED);
		txtGPSDisabled.setVisibility(View.VISIBLE);
	}

	@Override
	/**
	 * Notification to the user when GPS is enabled with toast and setting textView invisible on overview
	 */
	public void onGpsEnabled() {
		txtGPSDisabled.setVisibility(View.GONE);

	}

	@Override
	/**
	 * Notifications is GPS Signal changes e.g if you are in a tunnel and signal gets lost!
	 */
	public void onStatusChanged(int status) {
		if (status == LocationProvider.OUT_OF_SERVICE) {
			txtGPSDisabled.setText(GPS_NO_SIGNAL);
			txtGPSDisabled.setVisibility(View.VISIBLE);
		} else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
			txtGPSDisabled.setText(GPS_NO_SIGNAL_TEMPORARILY);
			txtGPSDisabled.setVisibility(View.VISIBLE);
		} else if (status == LocationProvider.AVAILABLE) {
			txtGPSDisabled.setVisibility(View.GONE);
		}
	}

	@Override
	public void onGpsUpdate(WayPoint w) {
		refreshGui(w);
	}

	/**
	 * Method to update the GUI after receiving a new WayPoint from GPS
	 * 
	 * @param w
	 *            new WayPoint
	 */
	private void refreshGui(WayPoint w) {
		if (isAdded() == false)
			return;
		
		showCarStateView(appContext.getEcar().getCarState());
		
		// checks that fragment is attached to the activity
		if (getActivity() == null
				|| getActivity().getApplicationContext() == null)
			return;
		
		Ecar ecar = appContext.getEcar();
		if (ecar != null & ecar.getBattery() != null) {
			Double batteryLeft = ecar.getBatteryPercentLeft();
			// update percentage ("50%") 
			txtAkkuState.setText("" + batteryLeft);
			// update progress bar
			int i_progress = batteryLeft.intValue();
			progressBar1.setProgress(i_progress);
		}

		// update everything not requiring a waypoint
		onCarStateUpdate(ecar.getCarState());
		if (ecar.getCarState() == Ecar.CarState.CHARGING
				& appContext.getEcar().getCurrentChargeSequence() != null) {
			String chargingPower = " ("
					+ appContext.getEcar().getCurrentChargeSequence()
							.getChargingType().getChargingPower() + "kWh)";
			txtCarState.setText(ecar.getStateString() + chargingPower);
		} else {
			txtCarState.setText(ecar.getStateString());
		}

		txtRemainingKmOnBattery.setText(AppContext.round(appContext.getEcar()
				.getRemainingKmOnBattery(), 2)
				+ " km");
		txtTimeTo100.setText(appContext.getTimeTo100Formatted());

		// do not attempt to update waypoint
		if (w == null)
			return;

		// update waypoint...
		txtCurrentSpeed.setText(appContext.round(w.getVelocity() * 3.6) + " km/h");

		if (appContext.ecar.getCurrentTrip() != null) {
			txtCoveredDistance.setText(appContext.round(appContext.ecar
					.getCurrentTrip().getCoveredDistance() / 1000) + " km");
		}
		String consumptionTxt = "0";
		if (w.getDistance() != 0) {
			consumptionTxt = "" + Math.round(
					// * 100'000 => consumption in kWh per 100 km
					(w.getEnergy() / w.getDistance()) * 100000);
		}
		consumptionTxt = consumptionTxt + " kWh";
		txtCurrentConsumption.setText(consumptionTxt);
		
		// Todo: change color to green yellow or white depending on acceleration 
		double currentConsumption = 0; 
		if (w.getDistance() != 0) 
			currentConsumption = (w.getEnergy() / w.getDistance() * 100000); 		
		if (currentConsumption > threshholdGreenAcceleration) 
			rootView.setBackgroundColor(getResources().getColor(R.color.color_red));
		else 
			rootView.setBackgroundColor(getResources().getColor(R.color.color_green));			
		
		// also update consumption in top notification menu ("Runterziehmenü") 
		appContext.showNotification(appContext.getEcar().getStateString(), consumptionTxt);		

		if (ecar.getCurrentTrip() != null && ecar.getBattery() != null) {
			double tripkWh = ecar.getCurrentTrip().getSocStart() - ecar.getBattery().getCurrentSoc();
			txtDebug1.setText(appContext.round(tripkWh) + " kWh");
		}
		
		String debugString = w.getUpdateType() + " "
				+ appContext.round(w.getDistance(), 0) + "m "
				+ appContext.round(w.getVelocity() * 3.6) + "km/h "
				+ appContext.round(w.getAcceleration(), 2) + "km/h/s "
				+ appContext.round(w.getEnergy()) + "kWh \n"
				+ appContext.round(ecar.getBatteryPercentLeft()) + "% "
				+ Math.round(ecar.getBattery().getCurrentSoc()) + "/"
				+ ecar.getVehicleType().getBatteryCapacity() + "kWh \n"
				+ w.getGeoCoordinate().toString() + " "
				+ w.getFormattedActivity() + "\n" + ecar.getStateString() + "";
		// L.v(debugString);

		txtDebug2.setText(debugString);
		
		String showDebugMessages = PreferenceManager
				.getDefaultSharedPreferences(appContext).getString(
						"testing.show_debug_messages", "disabled");
		// only show green debug messages if option is activated in app settings
		if (showDebugMessages.equals("show")) {
			txtDebug1.setVisibility(View.VISIBLE);
			txtDebug2.setVisibility(View.VISIBLE);
			txtDebug3.setVisibility(View.VISIBLE);
		} else {
			txtDebug1.setVisibility(View.INVISIBLE);
			txtDebug2.setVisibility(View.INVISIBLE);
			txtDebug3.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		v.performClick();
		L.d("onTouch()");
		return false;
	}

	@Override
	public void onClick(View v) {
		L.d("onClick()");
	}

}
