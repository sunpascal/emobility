package de.unibamberg.eesys.projekt.businessobjects;

import java.util.Calendar;
import java.util.List;

import android.widget.Toast;

import com.google.android.gms.location.DetectedActivity;

import de.unibamberg.eesys.projekt.AppContext;
import de.unibamberg.eesys.projekt.L;
import de.unibamberg.eesys.projekt.Params;
import de.unibamberg.eesys.projekt.ProactiveFeedback;
import de.unibamberg.eesys.projekt.businessobjects.Sequence.SequenceType;
import de.unibamberg.eesys.projekt.database.DatabaseException;

/**
 * Instance of this class represents the currently used Ecar model for
 * simulation
 * 
 * @author Pascal, Julia
 *
 */
public class Ecar {
	private AppContext appContext; // do not persist
	private long id = -1;
	private VehicleType vehicleType;
	private Battery battery;
	private String name;
	private String description;
	private CarState carState = CarState.PARKING_NOT_CHARGING; // todo: persist
																// in DB
	double startChargeTime = 0; // do not persist in DB

	/**
	 * time when car entered a stand still
	 */
	double timeLastMovement = -1; // do not persist in DB

	/*
	 * CO2 emission for producing 1kWh of elecritity in Germany (2013)
	 * http://de.
	 * statista.com/statistik/daten/studie/38897/umfrage/co2-emissionsfaktor
	 * -fuer-den-strommix-in-deutschland-seit-1990/
	 */
	private final int GRAM_CO2_PER_KWH = 559;

	private DriveSequence currentTrip; // not persisting
	private ChargeSequence currentChargeSequence;
	private ProactiveFeedback proactiveFeedback = null;

	public static enum CarState {
		DRIVING, CHARGING, PARKING_NOT_CHARGING;
	}

	/**
	 * Class Constructor with appContext Param
	 * 
	 * @param appContext
	 */
	public Ecar(AppContext appContext) {
		this.appContext = appContext;
		carState = CarState.PARKING_NOT_CHARGING;
	}

	/**
	 * 
	 * @return ID of ecar
	 */
	public long getId() {
		return id;
	}

	/**
	 * sets ECar ID
	 * 
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Type of the currently used Ecar
	 * 
	 * @return type of Vehicle
	 */
	public VehicleType getVehicleType() {
		return vehicleType;
	}

	/**
	 * sets vehicle type
	 * 
	 * @param vehicleType
	 */
	public void setVehicleType(VehicleType vehicleType) {
		this.vehicleType = vehicleType;
	}

	/**
	 * 
	 * @return battery of ECar
	 */
	public Battery getBattery() {
		return battery;
	}

	/**
	 * set battery of ecar
	 * 
	 * @param battery
	 */
	public void setBattery(Battery battery) {
		this.battery = battery;
	}

	/**
	 * calculates the battery state
	 * 
	 * @return percent of power left in battery
	 */
	public double getBatteryPercentLeft() {
		double left = (battery.getCurrentSoc() / vehicleType
				.getBatteryCapacity()) * 100;

		if (left <= 0)
			return 0;
		else
			// return result with two decimal places (nachkommastelle)
			return (double) Math.round(left * 100) / 100;
	}

	/**
	 * 
	 * @return ECar name
	 */
	public String getName() {
		return name;
	}

	/**
	 * sets name of ecar
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return ECar description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * sets ECar description
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public String toString() {
		return "id: " + id + "name: " + name + "descr: " + description;
		// "vehicleType: " + vehicleType.getName()
		// "battery SOC"
	}

	/**
	 * returns the state of the car: CHARGING, DRIVING or PARKING_NOT_CHARGING
	 * 
	 * @return
	 */
	public String getStateString() {
		String result = "Invalid car state.";

		if (carState == null)
			return result;

		switch (carState) {
		case CHARGING:
			return "Charging";
		case DRIVING:
			return "Driving";
		case PARKING_NOT_CHARGING:
			return "Parking, not charging";
		}
		return result;
	}

	/**
	 * starts charging and creates dataset for charge sequence. When charging is
	 * completed, updateCharging stores the remaining values in database
	 * 
	 * @param chargingStation
	 */
	public void chargeStart(ChargingStation chargingStation) {
		if (battery.getCurrentSoc() < 0) {
			battery.setCurrentSoc(0);
		}

		// start ChargeSequence

		L.d("Charging with "
				+ chargingStation.getChargingType().getChargingPower()
				+ "kWh at: " + chargingStation.getName() + " "
				+ chargingStation.getGeoCoordinate().toString());

		startChargeTime = Calendar.getInstance().getTimeInMillis();

		currentChargeSequence = new ChargeSequence();
		currentChargeSequence.setSequenceType(SequenceType.CHARGE);
		currentChargeSequence.setVehicleType(getVehicleType());
		currentChargeSequence.setSocStart(getBattery().getCurrentSoc());
		currentChargeSequence.setSocEnd(-1);
		currentChargeSequence.setTimeStart(Calendar.getInstance()
				.getTimeInMillis());
		currentChargeSequence.setTimeStop(-1);
		currentChargeSequence.setActive(true);
		currentChargeSequence
				.setChargingType(chargingStation.getChargingType());
		try {
			currentChargeSequence.setId(appContext.getDb().storeChargeSequence(
					currentChargeSequence));
		} catch (DatabaseException e) {
			Toast.makeText(appContext.getApplicationContext(),
					"An unexpected error has occurred.", Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();
		}
	}

	/**
	 * calculates charge progress since startChargeTime
	 * 
	 * call while regularly while charging + todo: Call when charging ends (e.g.
	 * car starts driving)
	 */
	public void updateCharging(ChargingStation chargingStation) {

		if (chargingStation == null) {
			String msg = "ChargingStation is null. This usually occurs during testing when carState = charging and then moving moving the simulated location too far away from the current charging station";
			L.wtf(msg);
			Toast.makeText(appContext.getApplicationContext(), msg,
					Toast.LENGTH_SHORT).show();
			return;
		}

		double now = Calendar.getInstance().getTimeInMillis();

		// Todo: check why duration is so small
		double durationMillis = now - startChargeTime;

		double durationInHours = durationMillis / 1000 / 3600;
		double chargedEnergy = chargingStation.charge(durationInHours);

		double currentSoc = battery.getCurrentSoc();

		double socAfterCharging = currentSoc + chargedEnergy;

		// make sure we don't charge more than 100%
		if (socAfterCharging > vehicleType.getBatteryCapacity()) {
			socAfterCharging = vehicleType.getBatteryCapacity();
		}

		battery.setCurrentSoc(socAfterCharging);
		L.d("Charge duration: " + durationMillis / 1000 + "s "
				+ "chargedEnergy: " + AppContext.round(chargedEnergy * 1000, 2)
				+ " _W_h " + "currentSoc: " + AppContext.round(currentSoc, 2)
				+ " kWh");

		// store in DB // todo: check if this should be called during charging
		// and not just at the end
		try {
			appContext.getDb().storeChargeSequence(currentChargeSequence);
			appContext.getDb().storeEcar(this);
		} catch (DatabaseException e) {
			// TODO check exception handling
			Toast.makeText(appContext.getApplicationContext(),
					"An unexpected error has occurred.", Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();
		}

		// set variable for next time updateCharging is called
		startChargeTime = now;

	}

	/**
	 * ends charging station and updates database
	 * 
	 */
	public void chargeStop() {

		// stop charge sequence
		currentChargeSequence.setSocEnd(getBattery().getCurrentSoc());
		currentChargeSequence.setTimeStop(Calendar.getInstance()
				.getTimeInMillis());
		currentChargeSequence.setActive(false);

		// store in DB
		try {
			appContext.getDb().storeChargeSequence(currentChargeSequence);
		} catch (DatabaseException e) {
			Toast.makeText(appContext.getApplicationContext(),
					"An unexpected error has occurred.", Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();
		}

	}

	/**
	 * creates a new trip, stores first values in database the remaining values
	 * are stored when endTrip() is called when the car changes from Driving to
	 * Parking state
	 */
	public void startTrip() {
		timeLastMovement = -1; // reset standstill time
		currentTrip = new DriveSequence();
		currentTrip.setSequenceType(SequenceType.DRIVE);

		// Set vehicle type: import deep copy:
		// That way the if vehicle type is changed by the user DURING a trip,
		// the consumption model will keep calculating using the vehicleType
		// that was
		// selected at the beginning of the trip
		VehicleType vehicleForConsumptionModel = new VehicleType(
				getVehicleType());
		currentTrip.setVehicleType(vehicleForConsumptionModel);

		currentTrip.setTimeStart(Calendar.getInstance().getTimeInMillis());
		currentTrip.setSocStart(getBattery().getCurrentSoc());
		currentTrip.setSocEnd(-1);
		currentTrip.setTimeStop(-1);
		currentTrip.setActive(true);
		currentTrip.setCoveredDistance(0);
		currentTrip.setSumCO2(0);

		// actually store trip
		try {
			currentTrip.setId(appContext.getDb()
					.storeDriveSequence(currentTrip));
		} catch (DatabaseException e) {
			Toast.makeText(appContext.getApplicationContext(),
					"An unexpected error has occurred.", Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();
		}

		// set trip
		appContext.getCurrentWayPoint().setDriveSequence(currentTrip);

		L.i("Starting trip: "
				+ appContext.getFormattedDateTime(currentTrip.getTimeStart()));
		
		proactiveFeedback  = new ProactiveFeedback();

	}

	/**
	 * called when trip must be ended for exception reasons such as GPS signal
	 * loss or if users disables GPS
	 */
	public void endTripAbnormal() {
		carState = CarState.PARKING_NOT_CHARGING;
		endTrip();
	}

	/**
	 * ends the trip by calculating remaining values (km, kWh) of current trip
	 * and storing them in the database
	 */
	private void endTrip() {
		// no trip started yet
		if (currentTrip == null) {
			String debugMsg = "endTrip(): Cannot end trip, because there is no current trip.";
			L.i(debugMsg);
			return;
		}

		// make sure any waypoints which have not yet been written to database,
		// are written now
		appContext.getBackgroundService().flushBuffer();

		currentTrip.setSocEnd(this.getBattery().getCurrentSoc());
		currentTrip.setTimeStop(Calendar.getInstance().getTimeInMillis());
		currentTrip.setActive(false);

		double sumCO2 = currentTrip.calcSumCO2(GRAM_CO2_PER_KWH,
				currentTrip.getCoveredDistanceInMeters());
		currentTrip.setSumCO2(sumCO2);

		// actually store trip
		try {
			appContext.getDb().storeDriveSequence(currentTrip);
			appContext.getDb().storeEcar(this);
			appContext.setLastTrip(currentTrip);
		} catch (DatabaseException e) {
			Toast.makeText(appContext.getApplicationContext(),
					"An unexpected error has occurred.", Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();
		}
		L.i("Ending trip: "
				+ appContext.getFormattedDateTime(currentTrip.getTimeStop())
				+ " "
				+ AppContext.round(currentTrip.getCoveredDistanceInMeters() / 1000, 2)
				+ "km");
	}

	/**
	 * checks what the current state should be if the state changes, executes
	 * necessary tasks
	 * 
	 * @param w
	 *            current Waypoint
	 * @param chargeStations
	 *            list of charge stations, used to check if near a charge
	 *            station ==> initiate charging
	 */
	public void updateCarState() {
		WayPoint w = appContext.getCurrentWayPoint();

		if (w == null)
			return;

		List<ChargingStation> chargeStations = appContext.getChargeStations();

		if (carState == null) {
			carState = CarState.PARKING_NOT_CHARGING;
			return;
		}

		if (carState == CarState.DRIVING) {

			// if moving, reset counter
			if (isVehicleStill(w) == false) {
				timeLastMovement = -1; // not relevant since car is moving
				// keep driving
			}
			// vehicle is still (<10 km/h) => Start timeout timer
			else if (timeLastMovement == -1) {
				// we have just stopped, store the time of stop
				timeLastMovement = System.currentTimeMillis();
			}
			// car has already stopped before - determine if threshold has been
			// exceeded
			else if (durationSinceLastMovementExceeded() == true) {
				timeLastMovement = -1; // reset counter for next time
				L.d("Vehicle stop time exceeds " + appContext.getParams().getMaxVehicleStillLocation()
						+ "s + ("
						+ (System.currentTimeMillis() - timeLastMovement)
						/ 1000 + "s). Ending trip.");
				Toast.makeText(
						appContext.getApplicationContext(),
						"Vehicle stop time exceeds "
								+ appContext.getParams().getMaxVehicleStillLocation()
								+ "s. Ending trip.", Toast.LENGTH_SHORT).show();
				endTrip();

				// we have to end trip - but go into charging or parking mode?
				if (!isBatteryFull()) {
					ChargingStation chargeStation = ChargingStation
							.getNearestChargeStation(w, chargeStations);
					if (chargeStation != null) {
						carState = CarState.CHARGING;
						chargeStart(chargeStation);
					} else {
						carState = CarState.PARKING_NOT_CHARGING;
					}
				} else {
					carState = CarState.PARKING_NOT_CHARGING;
				}
			}
			return;
		}

		if (carState == CarState.PARKING_NOT_CHARGING) {
			if (isVehicleMoving(w)) {
				carState = CarState.DRIVING;
				startTrip();
			}
			if (!isBatteryFull()) {
				ChargingStation chargeStation = ChargingStation
						.getNearestChargeStation(w, chargeStations);
				if (chargeStation != null) {
					carState = CarState.CHARGING;
					chargeStart(chargeStation);
				}
			}

			return;
		}

		if (carState == CarState.CHARGING) {
			if (isVehicleMoving(w)) {
				carState = CarState.DRIVING;
				chargeStop();
				startTrip();
			} else {
				if (isBatteryFull()) {
					carState = CarState.PARKING_NOT_CHARGING;
					chargeStop();
				} else {
					ChargingStation chargeStation = ChargingStation
							.getNearestChargeStation(w, chargeStations);
					if (chargeStation != null) {
						carState = CarState.CHARGING;
						updateCharging(chargeStation);
					}
				}

			}

		}
	}

	/**
	 * returns the current state of the car, i.e. DRIVING, CHARGING or
	 * PARKING_NOT_CHARGING;
	 * 
	 * @return
	 */
	public CarState getCarState() {
		return carState;
	}

	/**
	 * sets state of car
	 * 
	 * @param carState
	 *            either DRIVING, CHARGING or PARKING_NOT_CHARGING
	 */
	public void setCarState(CarState carState) {
		this.carState = carState;
	}

	/**
	 * 
	 * @return current Trip
	 */
	public DriveSequence getCurrentTrip() {
		return currentTrip;
	}

	/**
	 * sets current Trip
	 * 
	 * @param currentTrip
	 */
	public void setCurrentTrip(DriveSequence currentTrip) {
		this.currentTrip = currentTrip;
	}

	/**
	 * 
	 * @return instance of appContext
	 */
	public AppContext getAppContext() {
		return appContext;
	}

	/**
	 * sets appContext
	 * @param appContext
	 */
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
	
	/**
	 * Handle Car lifecycle within the different states 
	 */
	public void processLifecycle() {
		// when app is first started, it will not yet have a waypoint, therefore
		// skip
		if (appContext.getCurrentWayPoint() == null)
			return;

		updateCarState();

		WayPoint w = appContext.getCurrentWayPoint();

		if (carState == Ecar.CarState.DRIVING) {

			// get consumption data from GPS data
			// update GUI to show position and consumption data (w.getEnergy())

			// update battery state of charge
			// only update if locoation was changed, i.e. do not update if
			// merely the activity changed
			if (w.getUpdateType() == WayPoint.UpdateType.GPS) {
				double currentSOC = getBattery().getCurrentSoc();
				getBattery().setCurrentSoc(currentSOC - w.getEnergyInKWh());

				// update covered distance (cumulated distance of this trip so
				// far)
				getCurrentTrip()
						.setCoveredDistance(
								getCurrentTrip().getCoveredDistanceInMeters()
										+ w.getDistance());
				
				// set current soc in order to be able to calculate energy consumption so far
				getCurrentTrip().setCurrentSoc(currentSOC);
			}

		}

		else if (carState == Ecar.CarState.CHARGING) {
			// else if not moving anymore
			// check if charging station is nearby
			// if it is...
			ChargingStation cs = ChargingStation.getNearestChargeStation(w,
					appContext.getChargeStations());
			updateCharging(cs);

			// if ecar.isFullyCharged()
			// store trip in database incl. time and battery state
			// stop charging: Write charge time and battery state to database
		}

		else if (carState == Ecar.CarState.PARKING_NOT_CHARGING) {
			// don't do anything
		}

	}

	/**
	 * checks if car us currently moving This is the case if the car drives more
	 * than 10.9 km/h (or whatever value MIN_SPEED_FOR_MOVING is) *
	 * 
	 * @param w
	 * @return
	 */
	public boolean isVehicleMoving(WayPoint w) {

		if (appContext.ignoreActivityRecognition())
			return (w.getVelocity()) > Params.MIN_SPEED_FOR_MOVING;
		else
			return (w.getActivityType() == DetectedActivity.IN_VEHICLE
					||
					// let any type of movement count as driving a car (for
					// testing)
					// todo: In final version, move the following 4 to
					// isVehicleStill()
					w.getActivityType() == DetectedActivity.ON_BICYCLE
					|| w.getActivityType() == DetectedActivity.ON_FOOT
					|| w.getActivityType() == DetectedActivity.RUNNING || w
					.getActivityType() == DetectedActivity.WALKING)
					&& (w.getVelocity()) > Params.MIN_SPEED_FOR_MOVING;
	}

	/**
	 * checks if vehicle has been still for MAX_VEHICLE_STILL_DURATION seconds
	 * 
	 * allow stopping for short duration (e.g. traffic lights, traffic ) to
	 * count as "driving"
	 * 
	 * @return
	 */
	public boolean durationSinceLastMovementExceeded() {
		// -1 if vehicle has not yet moved
		if (timeLastMovement == -1)
			return false;

		double msStandstillDuration = System.currentTimeMillis()
				- timeLastMovement;
		if (msStandstillDuration > (appContext.getParams().getMaxVehicleStillLocation() * 1000))
			return true;
		else
			return false;
	}

	/**
	 * checks if car us currently still This is the case if the car drives less
	 * than 10.9 km/h (or whatever value MIN_SPEED_FOR_MOVING is)
	 * 
	 * @param w
	 * @return
	 */
	public boolean isVehicleStill(WayPoint w) {

		if (appContext.ignoreActivityRecognition())
			// only use velocity to decide
			return w.getVelocity() < Params.MIN_SPEED_FOR_MOVING;
		else
			// take activity recognition into account as well
			return (w.getVelocity() < Params.MIN_SPEED_FOR_MOVING
					&& (w.getActivityType()) == DetectedActivity.STILL
					|| w.getActivityType() == DetectedActivity.UNKNOWN || w
						.getActivityType() == DetectedActivity.TILTING);
	}

	/**
	 * checks if battery is fully charged
	 * 
	 * @return
	 */
	public boolean isBatteryFull() {
		return getBatteryPercentLeft() >= 100;
	}

	/**
	 * returns how much more time will be required until the battery is fully
	 * charged takes into account the current energy level of the battery
	 * 
	 * @return duration in ms, -1 if not currently charging
	 */
	public double getTimeTo100() {
		if (!(getCarState() == CarState.CHARGING))
			return -1;

		double currentSoc;
		if (getBattery().getCurrentSoc() < 0) {
			currentSoc = 0;
		} else
			currentSoc = getBattery().getCurrentSoc();

		double kWhLeftToCharge = getVehicleType().getBatteryCapacity()
				- currentSoc;

		double chargingPowerPerHour = currentChargeSequence.getChargingType()
				.getChargingPower();

		double timeLeftMs = (kWhLeftToCharge / chargingPowerPerHour) * 3600 * 1000;

		return timeLeftMs;

	}

	/**
	 * returns the reach, i.e. the remaining distance in km that can be driven
	 * with the current battery level
	 * 
	 * @return
	 */
	public double getRemainingKmOnBattery() {

		double currentSoc;
		if (getBattery().getCurrentSoc() <= 0) {
			currentSoc = 0;
		} else
			currentSoc = getBattery().getCurrentSoc();

		// calculate average energy consumption per km of last drive...
		List<DriveSequence> trips = null;
		try {
			trips = appContext.getDb().getDriveSequences(true);
		} catch (DatabaseException e) {
			Toast.makeText(appContext.getApplicationContext(),
					"An unexpected error has occurred.", Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();
		}

		double averagekWhPerKm;
		if (trips.size() > 0) {
			DriveSequence lastTrip = trips.get(trips.size() - 1);
			averagekWhPerKm = lastTrip.calcAveragekWhPerKm();
		} else {
			averagekWhPerKm = appContext.getEcar().getVehicleType()
					.getEnergyConsumption_perKM();
		}

		double remainingKm = currentSoc / averagekWhPerKm;
		return remainingKm;
	}

	/**
	 * returns the current charging sequence
	 * 
	 * @return
	 */
	public ChargeSequence getCurrentChargeSequence() {
		return currentChargeSequence;
	}

	public ProactiveFeedback getProactiveFeedback() {
		return proactiveFeedback;
	}

}
