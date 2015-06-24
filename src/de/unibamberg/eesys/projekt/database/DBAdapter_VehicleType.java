package de.unibamberg.eesys.projekt.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/**
 * Database Adapter to provide the SQL related methods for
 * operating on the database table "VehicleType".
 * 
 * @author Stefan
 * @version 1.0
 *
 */
public class DBAdapter_VehicleType {
	public static final String ROW_ID = Table_VehicleType.COLUMN_ID;
	public static final String NAME = Table_VehicleType.COLUMN_NAME;
	public static final String DESCRIPTION = Table_VehicleType.COLUMN_DESCRIPTION;
	public static final String BATTERY_CAPACITY = Table_VehicleType.COLUMN_BATTERY_CAPACITY;
	public static final String ENERGY_CONS_PER_KM = Table_VehicleType.COLUMN_ENERGY_CONSUMPTION_PER_KM;
	public static final String RECUPERATION_EFFICIENCY = Table_VehicleType.COLUMN_RECUPERATION_EFFICIENCY;
	public static final String MASS = Table_VehicleType.COLUMN_MASS;
	public static final String FRONT_AREA = Table_VehicleType.COLUMN_FRONT_AREA;
	public static final String PRICE = Table_VehicleType.COLUMN_PRICE;

    private static final String TABLE_VEHICLETYPE = Table_VehicleType.TABLE_NAME;

    private SQLiteDatabase mDb;

    /**
     * Constructor - reference the provided DB
     * 
     * @param dbAdapterDB
     * 		the database opened by the DBInitializer
     */
    public DBAdapter_VehicleType(SQLiteDatabase dbAdapterDB) {
        this.mDb = dbAdapterDB;
    }

    /**
     * Create a new VehicleType. If the VehicleType is successfully created return the new
     * rowId for that VehicleType, otherwise return a -1 to indicate failure.
     * 
     * @param name
     * @param description
     * @param batteryCapacity
     * @param energyConsumption_perKM
     * @param recuperationEfficiency
     * @param mass
     * @param frontArea
     * @return rowId or -1 if failed
     */
    public long createVehicleType(String name, String description, double batteryCapacity,
    		double energyConsumption_perKM, double recuperationEfficiency, double mass, double frontArea, String price) {
        ContentValues args = new ContentValues();
        args.put(NAME, name);
        args.put(DESCRIPTION, description);
        args.put(BATTERY_CAPACITY, batteryCapacity);
        args.put(ENERGY_CONS_PER_KM, energyConsumption_perKM);
        args.put(RECUPERATION_EFFICIENCY, recuperationEfficiency);
        args.put(MASS, mass);
        args.put(FRONT_AREA, frontArea);
        args.put(PRICE, price);
        return this.mDb.insert(TABLE_VEHICLETYPE, null, args);
    }

    /**
     * Delete the VehicleType with the given rowId
     * 
     * @param rowId
     * @return true if deleted, false otherwise
     */
    public boolean deleteVehicleType(long rowId) {

        return this.mDb.delete(TABLE_VEHICLETYPE, ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all VehicleTypes in the table
     * 
     * @return Cursor over all VehicleTypes
     */
    public Cursor getAllVehicleTypes() {

        return this.mDb.query(TABLE_VEHICLETYPE,
        		new String[] {ROW_ID, NAME, DESCRIPTION, BATTERY_CAPACITY, ENERGY_CONS_PER_KM,
        			RECUPERATION_EFFICIENCY, MASS, FRONT_AREA, PRICE },
        		null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the VehicleType that matches the given rowId
     * @param rowId
     * @return Cursor positioned to matching VehicleType (if found) or empty cursor
     */
    public Cursor getVehicleType(long rowId) {

        Cursor mCursor =

        this.mDb.query(true, TABLE_VEHICLETYPE, 
        		new String[] {ROW_ID, NAME, DESCRIPTION, BATTERY_CAPACITY, ENERGY_CONS_PER_KM,
        		RECUPERATION_EFFICIENCY, MASS, FRONT_AREA, PRICE},
        		ROW_ID + "=" + rowId, null, null, null, null, null);

        return mCursor;
    }

    /**
     * Update the VehicleType.
     * 
     * @param rowId
     * @param name
     * @param description
     * @param batteryCapacity
     * @param energyConsumption_perKM
     * @param recuperationEfficiency
     * @param mass
     * @param frontArea
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateVehicleType(long rowId, String name, String description, double batteryCapacity,
    		double energyConsumption_perKM, double recuperationEfficiency, double mass, double frontArea, String price) {
        ContentValues args = new ContentValues();
        args.put(NAME, name);
        args.put(DESCRIPTION, description);
        args.put(BATTERY_CAPACITY, batteryCapacity);
        args.put(ENERGY_CONS_PER_KM, energyConsumption_perKM);
        args.put(RECUPERATION_EFFICIENCY, recuperationEfficiency);
        args.put(MASS, mass);
        args.put(FRONT_AREA, frontArea);
        args.put(PRICE, price);
        return this.mDb.update(TABLE_VEHICLETYPE, args, ROW_ID + "=" + rowId, null) >0; 
    }

}