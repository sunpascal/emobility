package de.unibamberg.eesys.projekt.database;

import de.unibamberg.eesys.projekt.AppContext;

/**
 * test class for DatabaseImplementation class 
 * ToDo: use JUnit to run tests
 * 
 * @author stefan/pascal
 *
 */
public class DatabaseImplementationTest {
	
	private DBImplementation db; 
	private AppContext appContext;
	
	public DatabaseImplementationTest(DBImplementation db, AppContext appContext) {
		this.db = db; 
		this.appContext = appContext;
	}

	public void testDatabase() {
		// test database access
		
//		long csId;
//		List<ChargeSequence> css = new ArrayList<ChargeSequence>();
//		ChargeSequence cs = new ChargeSequence();
//		ChargingType ct = new ChargingType();
//		VehicleType vt = new VehicleType();
//		Battery ba = new Battery();
//		Ecar ec = new Ecar(appContext);
//		Ecar ec2 = new Ecar(appContext);
//		long ecId;
//		
//		vt.setId(3);
//		vt.setName("Tesla");
//		vt.setDescription("brumm el extremo!");
//		vt.setBatteryCapacity(85000);
//		vt.setEnergyConsumption_perKM(5);
//		vt.setRecuperationEfficiency(150);
//		vt.setMass(1800);
//		vt.setFrontArea((177 * 158) / 10000);
//		
//		ba.setCurrentSoc(33);
//		ba.setCharging(true);
//		
//		ec.setVehicleType(vt);
//		ec.setBattery(ba);
//		ec.setName("tolles eCar");
//		ec.setDescription("Beschreibung eines tollen eCars.");
//		
//		db.storeEcar(ec);
//		ec2 = db.getEcar();
//		ec2.getBattery().setCurrentSoc(44);
//		db.storeEcar(ec2);
//		
//		
//		ct.setId(2);
//		ct.setName("USL");
//		ct.setDescription("Ultra schnelles Laden");
//		ct.setChargingPower(300);
//		
//		cs.setSequenceType(SequenceType.CHARGE);
//		cs.setVehicleType(vt);
//		cs.setSocStart(85000);
//		cs.setSocEnd(80000);
//		cs.setTimeStart(123);
//		cs.setTimeStop(234);
//		cs.setActive(true);
//		cs.setChargingType(ct);
//		
//		csId = db.storeChargeSequence(cs);
//		css = db.getChargeSequences();
//		
//				ChargingStation chargingStation;
//		List<ChargingStation> charingStations;
//		
//		//ChargingStations abfragen
//		charingStations = db.getChargingStations();
//		chargingStation = charingStations.get(0);
//		
//		//ChargingStation abändern
//		chargingStation.setName("Uni Bamberg 2");
//		chargingStation.getChargingType().setId(1); //für DB ausreichend, aber für Java Objekt nicht empfehlenswert. Nur zum test.
//		chargingStation.getGeoCoordinate().setAltitude(1);
//		
//		//ChargingStation speichern (und erneut abfragen)
//		db.storeChargingStation(chargingStation);
//		charingStations = db.getChargingStations();
//		
//		
//		
//		DriveSequence ds = new DriveSequence();
//		VehicleType vt2 = new VehicleType();
//		
//		vt2.setId(3);
//		vt2.setName("Tesla");
//		vt2.setDescription("brumm el extremo!");
//		vt2.setBatteryCapacity(85000);
//		vt2.setEnergyConsumption_perKM(5);
//		vt2.setRecuperationEfficiency(150);
//		vt2.setMass(1800);
//		vt2.setFrontArea((177 * 158) / 10000);
//		
//		ds.setSequenceType(SequenceType.CHARGE);
//		ds.setVehicleType(vt2);
//		ds.setSocStart(85000);
//		ds.setSocEnd(80000);
//		ds.setTimeStart(123);
//		ds.setTimeStop(234);
//		ds.setActive(true);
//		ds.setSumConsumption(33);
//		ds.setSumCO2(22);
//		
//		
//		//@SuppressWarnings("unused")
//		long dsId = db.storeDriveSequence(ds);
//		
//		@SuppressWarnings("unused")
//		List<DriveSequence> driveSequences = new ArrayList<DriveSequence>();
//		driveSequences = db.getDriveSequences();
//		
//		
//		WayPoint w = new WayPoint();
//		GeoCoordinate g = new GeoCoordinate(7, 8, 9); 
//		DriveSequence d = new DriveSequence();
//		
//		w.setId(1);
//		w.setGeoCoordinate(g);
//		d.setId(1);
//		w.setDriveSequence(d);
//		w.setAcceleration(11);
//		w.setVelocity(12);
//		WayPoint[] waypointArray = new WayPoint[1]; 
//		waypointArray[0] = w;
//		db.storeWayPoints(waypointArray);
//		
////		List<WayPoint> wps = db.getWayPoints(d);
//		List<WayPoint> wps = db.getAllWayPoints();
//		
//		Calendar cal = Calendar.getInstance();
//		
//		DriveSequence ds1 = new DriveSequence();
//		DriveSequence ds2 = new DriveSequence();
//		DriveSequence ds3 = new DriveSequence();
//		DriveSequence ds4 = new DriveSequence();
//		DriveSequence ds5 = new DriveSequence();
//		DriveSequence ds6 = new DriveSequence();
//		DriveSequence ds7 = new DriveSequence();
//		VehicleType vt1 = new VehicleType();
//		VehicleType vt2 = new VehicleType();
//		VehicleType vt3 = new VehicleType();
//		
//		vt1.setId(1);
//		vt2.setId(2);
//		vt3.setId(3);
//		
//		ds1.setSequenceType(SequenceType.DRIVE);
//		ds1.setVehicleType(vt3);
//		ds1.setSocStart(17.6);
//		ds1.setSocEnd(3);
//		ds1.setTimeStart(123);
//		ds1.setTimeStop(234);
//		ds1.setActive(false);
//		ds1.setCoveredDistance(21);
//		ds1.setSumCO2(2);
//		
//		ds2.setSequenceType(SequenceType.DRIVE);
//		ds2.setVehicleType(vt1);
//		ds2.setSocStart(3);
//		ds2.setSocEnd(-5);
//		ds2.setTimeStart(123);
//		ds2.setTimeStop(234);
//		ds2.setActive(false);
//		ds2.setCoveredDistance(17);
//		ds2.setSumCO2(2);
//		
//		ds3.setSequenceType(SequenceType.DRIVE);
//		ds3.setVehicleType(vt1);
//		ds3.setSocStart(18);
//		ds3.setSocEnd(-22);
//		ds3.setTimeStart(123);
//		ds3.setTimeStop(234);
//		ds3.setActive(false);
//		ds3.setCoveredDistance(90);
//		ds3.setSumCO2(2);
//	
//		ds4.setSequenceType(SequenceType.DRIVE);
//		ds4.setVehicleType(vt1);
//		ds4.setSocStart(15);
//		ds4.setSocEnd(-3);
//		ds4.setTimeStart(123);
//		ds4.setTimeStop(234);
//		ds4.setActive(false);
//		ds4.setCoveredDistance(25);
//		ds4.setSumCO2(2);
//		
//		ds5.setSequenceType(SequenceType.DRIVE);
//		ds5.setVehicleType(vt1);
//		ds5.setSocStart(20);
//		ds5.setSocEnd(1);
//		ds5.setTimeStart(123);
//		ds5.setTimeStop(234);
//		ds5.setActive(false);
//		ds5.setCoveredDistance(22);
//		ds5.setSumCO2(2);
//		
//		ds6.setSequenceType(SequenceType.DRIVE);
//		ds6.setVehicleType(vt1);
//		ds6.setSocStart(22);
//		ds6.setSocEnd(3);
//		ds6.setTimeStart(cal.getTimeInMillis());
//		ds6.setTimeStop(cal.getTimeInMillis());
//		ds6.setActive(false);
//		ds6.setCoveredDistance(40);
//		ds6.setSumCO2(2);
//		
//		ds7.setSequenceType(SequenceType.DRIVE);
//		ds7.setVehicleType(vt1);
//		ds7.setSocStart(20);
//		ds7.setSocEnd(3);
//		ds7.setTimeStart(cal.getTimeInMillis());
//		ds7.setTimeStop(cal.getTimeInMillis());
//		ds7.setActive(false);
//		ds7.setCoveredDistance(35);
//		ds7.setSumCO2(2);
//
//		db.storeDriveSequence(ds1);
//		db.storeDriveSequence(ds2);
//		db.storeDriveSequence(ds1);
//		db.storeDriveSequence(ds3);
//		db.storeDriveSequence(ds4);
//		db.storeDriveSequence(ds1);
//		db.storeDriveSequence(ds1);
//		db.storeDriveSequence(ds5);
//		db.storeDriveSequence(ds6);
//		db.storeDriveSequence(ds7);
//		db.storeDriveSequence(ds1);
//		
//		
//		int[] bla;
//		bla = db.getDriveSeqFeasibility_Report();
		
//		Object bla = db.getReport_BatterySOCs(4);
		
//		double[] bla = db.getReport_AverageConsumption();

		
//		DriveSequence ds1 = new DriveSequence();
//		ChargeSequence cs1 = new ChargeSequence();
//		
//		ChargingType ct = new ChargingType();
//		
//		VehicleType vt1 = new VehicleType();
//		VehicleType vt2 = new VehicleType();
//		VehicleType vt3 = new VehicleType();
//		
//		vt1.setId(1);
//		vt2.setId(2);
//		vt3.setId(3);
//		
//		ct.setId(2);
//		ct.setName("USL");
//		ct.setDescription("Ultra schnelles Laden");
//		ct.setChargingPower(300);
//		
//		ds1.setSequenceType(SequenceType.DRIVE);
//		ds1.setVehicleType(vt1);
//		ds1.setSocStart(17.6);
//		ds1.setSocEnd(3);
//		ds1.setTimeStart(123);
//		ds1.setTimeStop(234);
//		ds1.setActive(false);
//		ds1.setCoveredDistance(21);
//		ds1.setSumCO2(2);
//
//		
//		cs1.setSequenceType(SequenceType.DRIVE);
//		cs1.setVehicleType(vt1);
//		cs1.setSocStart(17.6);
//		cs1.setSocEnd(3);
//		cs1.setTimeStart(123);
//		cs1.setTimeStop(234);
//		cs1.setActive(false);
//		cs1.setChargingType(ct);
//
//		ds1.setId(888);
//		db.storeDriveSequence(ds1);
		
		
//		db.storeDriveSequence(ds1);
//		
//		ds1.setId(75);
//		ds1.setId(db.storeDriveSequence(ds1));
//		
//		ds1.setCoveredDistance(999);
//		db.storeDriveSequence(ds1);
//		
//		ds1.setId(75);
//		ds1.setId(db.storeDriveSequence(ds1));		
//		
//		db.storeChargeSequence(cs1);
//		
//		cs1.setId(75);
//		cs1.setId(db.storeChargeSequence(cs1));
//		
//		cs1.setSocEnd(999);
//		db.storeChargeSequence(cs1);		
//		
		Object o = new Object();
		
		try {
			o = db.getReport_AverageConsumption(true);
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}