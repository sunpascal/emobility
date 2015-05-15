package de.unibamberg.eesys.projekt;

import de.unibamberg.eesys.projekt.businessobjects.VehicleType;

/**
 * Energy consumption interface contains all necessary methods to calculate the consumption of an ecar
 * @author Julia
 *
 */
public interface InterfaceEnergyConsumption {

	/** Constante is necessary for converting Jule to kWh */
	double constante = 3600000;
	/** Standard parameter for mid-sized cars */
	double coefficientRollingResistance = 0.011;
	double coefficientAirDrag = 0.29;
	double gravitationalAcceleration = 9.80665;
	double airDensity = 1.225;

	/**
	 * Class constructor: initialize values that are specific to car model
	 * Example call (BWM): double coefficientRollingResistance = 0.011; double
	 * coefficientAirDrag = 0.29; double batteryCapacity = 18.7; double
	 * chargingPower = 1.92; double mass = 1195; double area = (177 * 158) /
	 * 10000; EnergyConsumption ec = new EnergyConsumption
	 * (coefficientRollingResistance, coefficientAirDrag, mass, area);
	 * 
	 * @param coefficientRollingResistance
	 *            : no unit
	 * @param coefficientAirDrag
	 *            : no unit
	 * @param batteryCapacity
	 *            : in kWh
	 * @param chargingPower
	 *            : in kW
	 * @param mass
	 *            : in kg
	 * @param frontArea
	 *            : in m^2
	 */

	/**
	 * calculated the energy consumed between 2 points
	 * 
	 * if the car braked (negative acceleration), the consumed energy is
	 * negative, meaning the battery is charged
	 * 
	 * @param distance
	 *            in meters
	 * @param duration
	 *            in seconds
	 * @param startVelocity
	 *            in m/s
	 * @param endVelocity
	 *            in m/s
	 * @param acceleration
	 *            m/s^2
	 * @return the energy in kWh
	 */
	public double consumeEnergy(double duration, double startVelocity,
			double endVelocity, double distance, double accleration,
			double altitudeDiffInMeters, VehicleType vehicleType);

	/**
	 * returns how much energy (in kWh) is loaded into the battery while car is
	 * plugged in at charge station
	 * 
	 * @param duration
	 *            in seconds
	 * @param chargingPower
	 *            charge power that the charge station provides,
	 *            in kWh
	 * @return
	 */

}
