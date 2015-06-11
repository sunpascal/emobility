package de.unibamberg.eesys.projekt.businessobjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.DetectedActivity;

/**
 * Represents a way point of a DriveSequence and stores
 * the geo coordinate and additional information
 * e. g. velocity, acceleration
 * 
 * for info on nested parceable objects see
 * http://blog.logicexception.com/2012/09/a-parcelable-tutorial-for-android.html
 * 
 * @author pascal
 * 
 */
public class WayPoint implements Parcelable {

	long id;
	DriveSequence driveSequence;
	GeoCoordinate geoCoordinate;
	double acceleration = 0;
	long timestamp;

	private float distance = 0;  	// in meters
	private double velocity = 0.0;
	private int activityType = -2;
	private int activityConfidence = -2;

	private double energy = 0;  	// in kW
	private UpdateType updateType = UpdateType.NOT_SET;

	public static enum UpdateType {
		NOT_SET, ACTIVITY_RECOGNITION, GPS;
	}

	
	/**
	 * Constructor 
	 */
	public WayPoint() {
		geoCoordinate = new GeoCoordinate();
	}

	/**
	 * @return id of the WayPoint
	 */
	public long getId() {
		return id;
	}

	/**
	 * Set id  of the WayPoint
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Returns the GeoCoordinate of the WayPoint
	 * 
	 * @return GeoCoordinate
	 */
	public GeoCoordinate getGeoCoordinate() {
		return geoCoordinate;
	}

	/**
	 * Set the GeoCoordinate of the WayPoint
	 * 
	 * @param geoCoordinate
	 */
	public void setGeoCoordinate(GeoCoordinate geoCoordinate) {
		this.geoCoordinate = geoCoordinate;
	}

	/**
	 * Returns the current acceleration of the WayPoint
	 * 
	 * @return acceleration
	 */
	public double getAcceleration() {
		return acceleration;
	}

	/**
	 * Sets the current acceleration of the WayPoint
	 * 
	 * @param acceleration
	 */
	public void setAcceleration(double acceleration) {
		this.acceleration = acceleration;
	}

	/**
	 *  Returns the creation time (timestamp) of the WayPoint
	 * 
	 * @return
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets the creation time (timestamp) of the WayPoint
	 * 
	 * @param timestamp
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Returns the current velocity of the WayPoint
	 * 
	 * @return velocity in m/s
	 */
	public double getVelocity() {
		return velocity;
	}
	
	public double getVelocityinKmh() {
		return velocity * 3.6;
	}
	

	/**
	 * Sets the current velocity of the WayPoint
	 * 
	 * @param velocity
	 */
	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	/**
	 * Returns the current activity type (ActivityRecognition)
	 * 
	 *  0 = IN_VEHICLE 	The device is in a vehicle, such as a car.
	 *  1 = ON_BICYCLE 	The device is on a bicycle.
	 *  2 = ON_FOOT 	The device is on a user who is walking or running.
	 *  8 = RUNNING 	The device is on a user who is running.
	 *  3 = STILL 		The device is still (not moving).
	 *  5 = TILTING 	The device angle relative to gravity changed significantly.
	 *  4 = UNKNOWN 	Unable to detect the current activity.
	 *  7 = WALKING 	The device is on a user who is walking. 
	 * 
	 * @return activity type
	 */
	public int getActivityType() {
		return activityType;
	}

	/**
	 * Sets the current activity type (ActivityRecognition)
	 * 
	 *  0 = IN_VEHICLE 	The device is in a vehicle, such as a car.
	 *  1 = ON_BICYCLE 	The device is on a bicycle.
	 *  2 = ON_FOOT 	The device is on a user who is walking or running.
	 *  8 = RUNNING 	The device is on a user who is running.
	 *  3 = STILL 		The device is still (not moving).
	 *  5 = TILTING 	The device angle relative to gravity changed significantly.
	 *  4 = UNKNOWN 	Unable to detect the current activity.
	 *  7 = WALKING 	The device is on a user who is walking. 
	 * 
	 * @param activityType
	 */
	public void setActivityType(int activityType) {
		this.activityType = activityType;
	}

	/**
	 * Returns the confidence of the given activity type.
	 * 
	 * @return
	 */
	public int getActivityConfidence() {
		return activityConfidence;
	}

	/**
	 * Sets the confidence of the given activity type.
	 * 
	 * @param activityConfidence
	 */
	public void setActivityConfidence(int activityConfidence) {
		this.activityConfidence = activityConfidence;
	}

	/**
	 * Returns the Name and the Confidence of a activity
	 * 
	 * @return String ActivityName + Confidence
	 */
	public String getFormattedActivity() {
		return getActivityName() + " (" + activityConfidence + "%)";
	}

	/**
	 * Sets the Activity.
	 * 
	 * @param activityType
	 */
	public void setActivity(int activityType) {
		this.activityType = activityType;
	}

	/**
	 * Map detected activity types to strings
	 * 
	 * @param activityType
	 *            The detected activity type
	 * @return A user-readable name for the type
	 */
	public String getActivityName() {
		switch (activityType) {
		case DetectedActivity.IN_VEHICLE:
			return "in_vehicle";
		case DetectedActivity.ON_BICYCLE:
			return "on_bicycle";
		case DetectedActivity.ON_FOOT:
			return "on_foot";
		case DetectedActivity.STILL:
			return "still";
		case DetectedActivity.UNKNOWN:
			return "unknown";
		case DetectedActivity.TILTING:
			return "tilting";
		}
		return activityType + " (unknown activity type)";
	}

	/**
	 * Returns the distance since the last WayPoint
	 * 
	 * @return distance
	 */
	public float getDistance() {
		return distance;
	}

	/**
	 * Sets the distance since the last WayPoint
	 * 
	 * @param distance
	 */
	public void setDistance(float distance) {
		this.distance = distance;
	}

	/**
	 * Returns the corresponding DriveSequence of the WayPoint
	 * 
	 * @return DriveSequence
	 */
	public DriveSequence getDriveSequence() {
		return driveSequence;
	}

	/**
	 * Sets the corresponding DriveSequence of the WayPoint
	 * 
	 * @param driveSequence
	 */
	public void setDriveSequence(DriveSequence driveSequence) {
		this.driveSequence = driveSequence;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeDouble(acceleration);
		dest.writeLong(timestamp);
		dest.writeFloat(distance);
		dest.writeDouble(velocity);
		dest.writeInt(activityType);
		dest.writeInt(activityConfidence);
		dest.writeParcelable(geoCoordinate, flags);
		dest.writeDouble(energy);
	}

	/**
	 * This constructor is needed for the parcelable interface.
	 * It combines the primitive datatypes to a WayPoint object. 
	 * 
	 * @param in
	 */
	private WayPoint(Parcel in) {
		id = in.readLong();
		acceleration = in.readDouble();
		timestamp = in.readInt();
		distance = in.readFloat();
		velocity = in.readDouble();
		activityType = in.readInt();
		activityConfidence = in.readInt();
		geoCoordinate = in.readParcelable(GeoCoordinate.class.getClassLoader());
		energy = in.readDouble();
	}

	/**
	 * This method combines the primitive datatypes to a WayPoint object. 
	 */
	public static final Parcelable.Creator<WayPoint> CREATOR = new Parcelable.Creator<WayPoint>() {
		public WayPoint createFromParcel(Parcel in) {
			return new WayPoint(in);
		}

		/* (non-Javadoc)
		 * @see android.os.Parcelable.Creator#newArray(int)
		 */
		public WayPoint[] newArray(int size) {
			return new WayPoint[size];
		}
	};

	/**
	 * Returns the consume energy in kWhto since the last WayPoint.
	 * 
	 * @return
	 */
	public double getEnergyInKWh() {
		return energy;
	}

	/**
	 * Set the needed energy to since the last WayPoint.
	 * 
	 * @param energy
	 */
	public void setEnergy(double energy) {
		this.energy = energy;
	}

	/**
	 * Returns the current UpdateType.
	 * NOT_SET, ACTIVITY_RECOGNITION, GPS
	 * 
	 * @return UpdateType
	 */
	public UpdateType getUpdateType() {
		return updateType;
	}

	/**
	 * Sets the current UpdateType.
	 * NOT_SET, ACTIVITY_RECOGNITION, GPS
	 * 
	 * @param updateType
	 */
	public void setUpdateType(UpdateType updateType) {
		this.updateType = updateType;
	}

	/** 
	 * calculates the consumption in kWh per km
	 */
	public double calcConsumptionPerKm() {
		return energy / (distance / 1000) ;
	}
	
	/** 
	 * calculates the consumption in kWh per 100 km
	 * @return
	 */
	public double calcConsumptionPer100km() {
		return calcConsumptionPerKm() * 100;
	}	
}