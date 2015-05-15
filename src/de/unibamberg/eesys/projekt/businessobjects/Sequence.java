package de.unibamberg.eesys.projekt.businessobjects;

/**
 * @author Stefan
 * the Sequence represents a driving sequence between the start- and endpoint of a trip.
 *
 */
public class Sequence {

	public enum SequenceType {
		DRIVE, CHARGE;
	}

	private long id;
	private SequenceType sequenceType;
	private VehicleType vehicleType;
	private double socStart; // the energy contained in the battery at trip
								// start in kWh
	private double socEnd; // energy at end of trip, in kWh
	private long timeStart; // starttime of this Sequence.
	private long timeStop; // stoptime of this Sequence.
	boolean active; // indicates it a Sequence is active or closed.

	/**
	 * @return the current ID.
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id of the sequence is setted.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the current sequence Type
	 */
	public SequenceType getSequenceType() {
		return sequenceType;
	}

	/**
	 * @param sequenceType current sequencetype is setted.
	 */
	public void setSequenceType(SequenceType sequenceType) {
		this.sequenceType = sequenceType;
	}

	/**
	 * @return the vehicleType that is connected to this sequence.
	 */
	public VehicleType getVehicleType() {
		return vehicleType;
	}

	/**
	 * @param vehicleType that is connected to this sequence is setted.
	 */
	public void setVehicleType(VehicleType vehicleType) {
		this.vehicleType = vehicleType;
	}

	/**
	 * @return the Battery state of charge at the beginning of the Sequence
	 */
	public double getSocStart() {
		return socStart;
	}

	/**
	 * @param socStart sets the Battery state of charge at the beginning of the Sequence
	 */
	public void setSocStart(double socStart) {
		this.socStart = socStart;
	}
	/**
	 * @param socEnd battery state of charge. 
	 */
	public double getSocEnd() {
		return socEnd;
	}

	/**
	 * @param socEnd battery state of charge can be setted.
	 */
	public void setSocEnd(double socEnd) {
		this.socEnd = socEnd;
	}
	/**
	 * @return gets the starting time of the Sequence.
	 */
	public long getTimeStart() {
		return timeStart;
	}

	/**
	 * @param timeStart of the Sequence can be setted.
	 */
	public void setTimeStart(long timeStart) {
		this.timeStart = timeStart;
	}

	/**
	 * @return gets the ending time of the Sequence.
	 */
	public long getTimeStop() {
		return timeStop;
	}

	/**
	 * @param l the endingtime of the Sequence can be setted.
	 */
	public void setTimeStop(long l) {
		this.timeStop = l;
	}

	/**
	 * @return the current active state.
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the current sequence is set active.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

}
