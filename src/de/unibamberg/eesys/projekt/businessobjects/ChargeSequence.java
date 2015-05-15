package de.unibamberg.eesys.projekt.businessobjects;

/**
 * The ChargeSequence represents the charging procedure of the
 * eCar and provides charging related information.
 * 
 * @author Stefan
 * @version 1.0
 *
 */
public class ChargeSequence extends Sequence {

	private ChargingType chargingType;

	/**
	 * Provides the used charging type for this charge sequence
	 * 
	 * @return charging type (business object)
	 */
	public ChargingType getChargingType() {
		return chargingType;
	}

	/**
	 * Sets the charging type for this charge sequence
	 * 
	 * @param chargingType (business object)
	 */
	public void setChargingType(ChargingType chargingType) {
		this.chargingType = chargingType;
	}
}
