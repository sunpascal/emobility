package de.unibamberg.eesys.projekt;

import android.util.Log;
import de.unibamberg.eesys.projekt.businessobjects.VehicleType;

/**
 * Class Calculates the amount of energy, which would be used for an driven track.
 * @author Pascal, Matthias
 *
 */
public class HlpEnergyConsumptionModel implements InterfaceEnergyConsumption {

	private double consumedEnergy = 0;
	/**
	 * gets the consumed energy
	 * @return currently consumed energy
	 */
	public double getEnergy() {
		return consumedEnergy;
	}
	/**
	 * calculates the consumed energy in kWh
	 * @param duration
	 * @param startVelocity
	 * @param endVelocity
	 * @param distance
	 * @param vehicleType
	 * @return energy in kWh
	 * 
	 */
	public double consumeEnergy(double duration, double startVelocity,
			double endVelocity, double distance, double acceleration,
			double hightDelta, VehicleType vehicleType) {
		/**
		 * Calculates the amount of energy, which would be used for an driven
		 * track.
		 * 
		 * @pre double Variables distance and duration must be set before in
		 *      GeoCoordinates
		 * @post A double value is returned to the Ecar
		 */

		//Formula Fad  (11)
		double forceAirDrag = 0.5 * vehicleType.getFrontArea()
				* coefficientAirDrag * endVelocity * endVelocity * airDensity;

		//Formula Frr (12)
		double forceRollingResistance = coefficientRollingResistance
				* gravitationalAcceleration * vehicleType.getMass();

		//Formula Fla (14)
		//increase the mass of the vehicle by 5 % to approximate formula Fwa (15) consumption model
		double forceAcceleration = (vehicleType.getMass() * 1.05) * acceleration;

		//Formula Fhc
		double hillClimbingForce;
/*		Das ist Quatsch - statt sin() muss einfach die Höhe verwendet werden!!
 		* 	double hillClimbingForce = vehicleType.getMass()
				* gravitationalAcceleration
				* (Math.sin((100 * altitudeDiffInMeters / distance) / 100));
		*/
		// Berechnung der Energie beim Bergauffahren benötigt Steigungswinkel α
		// GPS liefert Höhe (h) und Distanz (d) seit letztem Puntk 
		// =>  tan α = h/d
		// =>   α = tan-1 a (h/d)
		// todo: muss 
		if (distance == 0) {
			L.e("hillClimbingForce konnte nicht berechnet werden, da distance=0");
			hillClimbingForce = 0; 
		}
		else { 
//			=> ToDo: was if driving downhill? 
//			double steigungswinkel = Math.toDegrees(Math.atan(hightDelta / distance));
			hillClimbingForce = vehicleType.getMass()
				* gravitationalAcceleration
				* (hightDelta/distance);    // höhenunterschied pro Meter!
//			L.v("hillClimbingForce=" + hillClimbingForce + ", hillClimbingForce=" + hillClimbingForce);
		}

		double recuperation;
		
		if (acceleration < 0) {
		// Adds recuperation if car breaks
			// Coefficient 0.5 to approximate losses
			// --> very different depending on speed, deceleration rate etc
			recuperation = 0.5 * (forceAcceleration + forceAirDrag
					+ forceRollingResistance + hillClimbingForce);

			consumedEnergy = (recuperation * distance) / constante;
			return consumedEnergy;
		} else {
		// Adds energy if car drives
			// Denominator 0.8 to approximate losses of the overall system
			// (heating, drive train, etc.)
			// --> very different depending on temperature (heating, cooling), vehicle type, etc.
			double totalTractiveEffort = (forceAirDrag + forceRollingResistance
					+ forceAcceleration + hillClimbingForce) / 0.8;
			
			consumedEnergy = ((totalTractiveEffort) * (distance)) / constante;
			
			return consumedEnergy;
		}
	}
}
