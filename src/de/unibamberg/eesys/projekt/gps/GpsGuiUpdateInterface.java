/**
 * @author robert lang
 */

package de.unibamberg.eesys.projekt.gps;

/*
 * The interface is implemented by the activity which displays the real-time reports. 
 * It provides the needed callbacks.
 */
public interface GpsGuiUpdateInterface {
	public void onAkkuUpdate(String akkuValue);

	public void onSpeedUpdate(String speed);

	public void onConsumptionUpdate(String consumedEnergy);

	public void onRemainingTimeOnBatteryUpdate(String timeOnBattery);

	public void onCharchingTimeRemainingUpdate(String time);

	public void onCoveredDistanceUpdate(String coveredDistance);

	public void onCarStateUpdate(String carState);

}
