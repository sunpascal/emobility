package de.unibamberg.eesys.projekt.businessobjects;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * Class represents instance of a GeoCoordinate
 * @author Julia
 *
 */
public class GeoCoordinate implements Parcelable {

	private long id;
	private double longitude;
	private double latitude;
	private double altitude;
	
	/**
	 * empty Constructor of GeoCoordinate
	 */
	public GeoCoordinate() {

	}
	/**
	 * Constructor with longitude and latitude
	 * @param longitude2
	 * @param latitude2
	 */
	public GeoCoordinate(double longitude2, double latitude2) {
		longitude = longitude2;
		latitude = latitude2;
		altitude = 0;
	}
	/**
	 * Constructor with longitude, latitude and altitude
	 * @param longitude2
	 * @param latitude2
	 * @param altitude2
	 */
	public GeoCoordinate(double longitude2, double latitude2, double altitude2) {
		longitude = longitude2;
		latitude = latitude2;
		altitude = altitude2;
	}
	/**
	 * 
	 * @return id of GeoCoordinate
	 */
	public long getId() {
		return id;
	}
	/**
	 * sets id of GeoCoordinate
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * 
	 * @return longitude of GeoCoordinate
	 */
	public double getLongitude() {
		return longitude;
	}
	/**
	 * sets longitude of GeoCoordinate
	 * @param longitude
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	/**
	 * 
	 * @return latitude of GeoCoordinate
	 */
	public double getLatitude() {
		return latitude;
	}
	/**
	 * sets latitude of GeoCoordinate
	 * @param latitude
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	/**
	 * 
	 * @return altitude of GeoCoordinate
	 */
	public double getAltitude() {
		return altitude;
	}
	/**
	 * sets altitude of GeoCoordinate
	 * @param altitude
	 */
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public String toString() {
		String result = (double) Math.round(getLatitude() * 10000) / 10000
				+ ", " + (double) Math.round(getLongitude() * 10000) / 10000;
		if (altitude != -1)
			return result + " " + altitude + "m";
		else
			return result;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeDouble(longitude);
		dest.writeDouble(latitude);
		dest.writeDouble(altitude);

	}
	/**
	 * private Constructor to parse latitude longitude and altitude 
	 * @param in
	 */
	private GeoCoordinate(Parcel in) {
		id = in.readLong();
		longitude = in.readDouble();
		latitude = in.readDouble();
		altitude = in.readDouble();
	}

	public static final Parcelable.Creator<GeoCoordinate> CREATOR = new Parcelable.Creator<GeoCoordinate>() {
		public GeoCoordinate createFromParcel(Parcel in) {
			return new GeoCoordinate(in);
		}

		public GeoCoordinate[] newArray(int size) {
			return new GeoCoordinate[size];
		}
	};

}
