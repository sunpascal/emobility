package de.unibamberg.eesys.projekt.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/**
 * Database Adapter to provide the SQL related methods for
 * operating on the database table "GeoCoordinate".
 * 
 * @author Stefan
 * @version 1.0
 *
 */
public class DBAdapter_GeoCoordinate{
	public static final String ROW_ID = Table_GeoCoordinate.COLUMN_ID;
	public static final String LONGITUDE = Table_GeoCoordinate.COLUMN_LONGITUDE;
	public static final String LATITUDE = Table_GeoCoordinate.COLUMN_LATITUDE;
	public static final String ALTITUDE = Table_GeoCoordinate.COLUMN_ALTITUDE;

    private static final String TABLE_GEOCOORDINATE = Table_GeoCoordinate.TABLE_NAME;

    private SQLiteDatabase mDb;

    /**
     * Constructor - reference the provided DB
     * 
     * @param dbAdapterDB
     * 		the database opened by the DBInitializer
     */
    public DBAdapter_GeoCoordinate(SQLiteDatabase dbAdapterDB) {
        this.mDb = dbAdapterDB;
    }

    /**
     * Create a new GeoCoordinate. If the GeoCoordinate is successfully created return the new
     * rowId for that GeoCoordinate, otherwise return a -1 to indicate failure.
     * 
     * @param longitude
     * @param latitude
     * @param altitude
     * @return rowId or -1 if failed
     */
    public long createGeoCoordinate(double longitude, double latitude, double altitude) {
        ContentValues args = new ContentValues();
        args.put(LONGITUDE, longitude);
        args.put(LATITUDE, latitude);
        args.put(ALTITUDE, altitude);
        return this.mDb.insert(TABLE_GEOCOORDINATE, null, args);
    }

    /**
     * Delete the GeoCoordinate with the given rowId
     * 
     * @param rowId
     * @return true if deleted, false otherwise
     */
    public boolean deleteGeoCoordinate(long rowId) {

        return this.mDb.delete(TABLE_GEOCOORDINATE, ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all GeoCoordinates in the table
     * 
     * @return Cursor over all GeoCoordinates
     */
    public Cursor getAllGeoCoordinates() {

        return this.mDb.query(TABLE_GEOCOORDINATE,
        		new String[] {ROW_ID, LONGITUDE, LATITUDE, ALTITUDE },
        		null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the GeoCoordinate that matches the given rowId
     * @param rowId
     * @return Cursor positioned to matching GeoCoordinate (if found) or empty cursor
     */
    public Cursor getGeoCoordinate(long rowId) {

        Cursor mCursor =

        this.mDb.query(true, TABLE_GEOCOORDINATE, 
        		new String[] {ROW_ID, LONGITUDE, LATITUDE, ALTITUDE },
        		ROW_ID + "=" + rowId, null, null, null, null, null);

        return mCursor;
    }

    /**
     * Update the GeoCoordinate.
     * 
     * @param rowId
     * @param longitude
     * @param latitude
     * @param altitude
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateGeoCoordinate(long rowId, double longitude, double latitude, double altitude) {
        ContentValues args = new ContentValues();
        args.put(LONGITUDE, longitude);
        args.put(LATITUDE, latitude);
        args.put(ALTITUDE, altitude);

        return this.mDb.update(TABLE_GEOCOORDINATE, args, ROW_ID + "=" + rowId, null) >0; 
    }

}