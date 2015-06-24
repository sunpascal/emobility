package de.unibamberg.eesys.projekt.businessobjects;

/**
 * @author r
 *	Represents an ECartype, it is the central place for ECartype specific information. 
 */
public class VehicleType implements Comparable {
	private long id;
	private String name;
	private String description;
	private double energyConsumption_perKM;
	private double recuperationEfficiency;

	private double mass;
	private double frontArea;
	private double batteryCapacity;
	
	private String price;
	
	// Recommendations: number of trips that have to be done using a different vehicles
	private int nTripAdaptationsrequired = -1;			// not persisted
	
	private String percentConsumptionReductionRequired = "";

	/**
	 * standard constructor
	 */
	public VehicleType() {

	}

	/**
	 * constructor
	 * @param id a valid ID
	 * @param description description of the VehicleType
	 * @param mass the current mass of the type
	 * @param frontArea Drag coefficient of the VehicleType
	 * @param batteryCapacity of the VehicleType
	 */
	public VehicleType(long id, String description, double mass,
			double frontArea, double batteryCapacity, String price) {
		this.id = id;
		this.description = description;
		this.mass = mass;
		this.frontArea = frontArea;
		this.batteryCapacity = batteryCapacity;
		this.price = price;
	}

	/**
	 * @param vehicleType a completed vehicleType can be used to instanciate this class
	 */
	public VehicleType(VehicleType vehicleType) {
		this.id = vehicleType.id;
		this.name = vehicleType.name;
		this.description = vehicleType.description;
		this.energyConsumption_perKM = vehicleType.energyConsumption_perKM;
		this.recuperationEfficiency = vehicleType.recuperationEfficiency;
		this.mass = vehicleType.mass;
		this.frontArea = vehicleType.frontArea;
		this.batteryCapacity = vehicleType.batteryCapacity;
		this.price = vehicleType.price;
	}

	/**
	 * @return the current id.
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id is setted
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the name of the VehicleType
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name of the VehicleType can be setted.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the VehicleTypeDescritption
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the descriptiontext of the VehicleType can be setted.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the Energy Consumption per kilometer
	 */
	public double getEnergyConsumption_perKM() {
		return energyConsumption_perKM;
	}

	/**
	 * @param sets the Energy Consumption per kilometer
	 */
	public void setEnergyConsumption_perKM(double energyConsumption_perKM) {
		this.energyConsumption_perKM = energyConsumption_perKM;
	}

	/**
	 * @return the Recuperation Efficiency
	 */
	public double getRecuperationEfficiency() {
		return recuperationEfficiency;
	}

	/**
	 * @param recuperationEfficiency is set
	 */
	public void setRecuperationEfficiency(double recuperationEfficiency) {
		this.recuperationEfficiency = recuperationEfficiency;
	}

	/**
	 * @return the current mass
	 */
	public double getMass() {
		return mass;
	}

	/**
	 * @param mass is set
	 */
	public void setMass(double mass) {
		this.mass = mass;
	}

	/**
	 * @return the air drag coefficient of the car is returned 
	 */
	public double getFrontArea() {
		return frontArea;
	}
	
	/**
	 * @param frontArea - the air drag coefficient of the car can be setted.
	 */
	public void setFrontArea(double frontArea) {
		this.frontArea = frontArea;
	}

	/**
	 * @return gets the battery capacity of the vehicle Type
	 */
	public double getBatteryCapacity() {
		return batteryCapacity;
	}

	/**
	 * @param batteryCapacity of the vehicle type can be setted
	 */
	public void setBatteryCapacity(double batteryCapacity) {
		this.batteryCapacity = batteryCapacity;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// the to Stringmethod of the vehicletype returns the current name.
		return name;
	}

	@Override
	public int compareTo(Object another) {
		if (batteryCapacity > ((VehicleType) another).getBatteryCapacity())
			return 1;
		else if (batteryCapacity < ((VehicleType) another).getBatteryCapacity())
			return -1; 
		else return 0;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public int getnTripAdaptationsrequired() {
		return nTripAdaptationsrequired;
	}

	public void setnTripAdaptationsrequired(int nTripAdaptationsrequired) {
		this.nTripAdaptationsrequired = nTripAdaptationsrequired;
	}

	public String getPercentConsumptionReductionRequired() {
		return percentConsumptionReductionRequired;
	}

	public void setPercentConsumptionReductionRequired(
			String percentConsumptionReductionRequired) {
		this.percentConsumptionReductionRequired = percentConsumptionReductionRequired;
	}

}